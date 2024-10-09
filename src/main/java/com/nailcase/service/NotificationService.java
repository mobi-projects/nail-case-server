package com.nailcase.service;

import static com.nailcase.exception.codes.NotificationErrorCode.*;
import static com.nailcase.exception.codes.ReservationErrorCode.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.nailcase.exception.BusinessException;
import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.dto.NotificationReadDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.entity.Notification;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.EmitterRepository;
import com.nailcase.repository.NotificationRepository;
import com.nailcase.repository.ReservationDetailRepository;
import com.nailcase.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final static Long DEFAULT_TIMEOUT = 3600000L;
	private final static String NOTIFICATION_NAME = "notify";

	private final EmitterRepository emitterRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationPersistenceService notificationPersistenceService;
	private final ReservationDetailRepository reservationDetailRepository;

	public SseEmitter connectNotification(UserPrincipal userPrincipal) {
		String emitterKey = generateEmitterKey(userPrincipal.id(), userPrincipal.role());
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
		emitterRepository.save(emitterKey, sseEmitter);

		sseEmitter.onCompletion(() -> emitterRepository.delete(emitterKey));
		sseEmitter.onTimeout(() -> emitterRepository.delete(emitterKey));

		try {
			// 미전송 알림 즉시 전송
			sendUnsentNotifications(userPrincipal.id(), userPrincipal.role(), sseEmitter);
		} catch (Exception exception) {
			emitterRepository.delete(emitterKey);
			throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
		}
		return sseEmitter;
	}

	private void sendUnsentNotifications(Long userId, Role role, SseEmitter emitter) {
		List<Notification> unsentNotification = notificationRepository.findByTypeAndReceiverIdWithNotRead(userId,
			role);
		for (Notification notification : unsentNotification) {
			if (notification != null) {
				try {
					NotificationDto.Response response = convertToResponse(notification);
					emitter.send(SseEmitter.event()
						.id(notification.getNotificationId().toString())
						.name(NOTIFICATION_NAME)
						.data(response));
					notification.updateSent();
					notificationPersistenceService.saveNotification(notification);
				} catch (IOException e) {
					log.error("Failed to send unsent notification: {}", notification.getNotificationId(), e);
				}
			}
		}
	}

	// 클라이언트로의 실시간 전송을 위한 별도의 메서드
	@Transactional
	public void processAndSendNotification(NotificationDto.Request request) {
		Notification notification = createNotification(request);
		notification = notificationPersistenceService.saveNotification(notification);
		log.info("Notification saved with ID: {}", notification.getNotificationId());

		boolean sent = sendToClient(request, notification);
		if (sent) {
			notification.updateSent();
			notification = notificationPersistenceService.saveNotification(notification);
			log.info("Notification marked as sent and updated with ID: {}", notification.getNotificationId());
		} else {
			log.info("Notification saved but not sent. ID: {}. It will be available when the user connects.",
				notification.getNotificationId());
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendNotificationToClient(NotificationDto.Request request) {
		processAndSendNotification(request);
	}

	private boolean sendToClient(NotificationDto.Request request, Notification notification) {
		Role role;
		if (request.getNotificationType() == NotificationType.RESERVATION_REQUEST
			|| request.getNotificationType() == NotificationType.RESERVATION_CANCEL) {
			role = Role.MANAGER;
		} else {
			role = Role.MEMBER;
		}

		String emitterKey = generateEmitterKey(request.getReceiverId(), role);
		return emitterRepository.get(emitterKey).map(sseEmitter -> {
			try {
				NotificationDto.Response response = convertToResponse(notification);
				String id = (notification.getNotificationId() != null) ?
					notification.getNotificationId().toString() :
					"temp-" + System.currentTimeMillis();
				sseEmitter.send(SseEmitter.event()
					.id(id)
					.name(NOTIFICATION_NAME)
					.data(response));
				log.info("Notification sent successfully to user: {}, ID: {}", request.getReceiverId(), id);
				return true;
			} catch (IOException exception) {
				emitterRepository.delete(emitterKey);
				log.error("Failed to send notification: {} to user: {}", notification.getNotificationId(),
					request.getReceiverId(),
					exception);
				return false;
			}
		}).orElseGet(() -> {
			log.info("No active connection for user: {}. Notification ID: {} saved for later delivery.",
				request.getReceiverId(),
				notification.getNotificationId());
			return false;
		});
	}

	@Transactional
	public void markAsRead(NotificationReadDto notificationReadDto) {
		Notification notification = notificationRepository.findById(notificationReadDto.getNotificationId())
			.orElseThrow(() -> new BusinessException(NOTIFICATION_NOT_FOUND));
		notification.markAsRead();
		notificationPersistenceService.saveNotification(notification);
	}

	private String generateEmitterKey(Long userId, Role role) {
		return "Emitter:UID:" + userId + ":ROLE:" + role;
	}

	private Notification createNotification(NotificationDto.Request request) {
		ReservationDetail reservationDetail = reservationDetailRepository.findById(request.getReservationId())
			.orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND));
		return Notification.builder()
			.content(request.getContent())
			.senderId(request.getSenderId())
			.receiverId(request.getReceiverId())
			.notificationType(request.getNotificationType())
			.reservationDetail(reservationDetail)
			.build();
	}

	public List<NotificationDto.Response> getNotifications(Long userId, Role role) {
		List<Notification> notification;

		if (role == Role.MEMBER) {
			notification = notificationRepository.findByMemberReceiverId(userId);
		} else {
			notification = notificationRepository.findByNailArtistReceiverId(userId);
		}

		return notification.stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}

	private NotificationDto.Response convertToResponse(Notification notification) {
		NotificationDto.Response response = new NotificationDto.Response();
		response.setNotificationId(notification.getNotificationId());
		response.setContent(notification.getContent());
		response.setNotificationType(notification.getNotificationType());
		response.setSendDateTime(DateUtils.localDateTimeToUnixTimeStamp(notification.getCreatedAt()));
		response.setSenderId(notification.getSenderId());
		response.setReceiverId(notification.getReceiverId());
		response.setRead(notification.isRead());
		response.setReservationId(notification.getReservationDetail().getReservationDetailId());
		response.setStartTime(
			DateUtils.localDateTimeToUnixTimeStampWithNull(notification.getReservationDetail().getStartTime()));
		response.setEndTime(
			DateUtils.localDateTimeToUnixTimeStampWithNull(notification.getReservationDetail().getEndTime()));

		return response;
	}

}
