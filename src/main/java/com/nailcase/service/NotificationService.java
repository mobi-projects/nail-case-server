package com.nailcase.service;

import static com.nailcase.exception.codes.NotificationErrorCode.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.nailcase.exception.BusinessException;
import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.entity.Notification;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.EmitterRepository;
import com.nailcase.repository.NotificationRepository;

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

	public SseEmitter connectNotification(UserPrincipal userPrincipal) {
		String emitterKey = generateEmitterKey(userPrincipal.id(), userPrincipal.role());
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
		emitterRepository.save(emitterKey, sseEmitter);

		sseEmitter.onCompletion(() -> emitterRepository.delete(emitterKey));
		sseEmitter.onTimeout(() -> emitterRepository.delete(emitterKey));

		try {
			sseEmitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("Connection completed"));
		} catch (IOException exception) {
			emitterRepository.delete(emitterKey);
			throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
		}
		return sseEmitter;
	}

	// 클라이언트로의 실시간 전송을 위한 별도의 메서드
	public void sendNotificationToClient(NotificationDto.Request request) {
		Notification notification = createNotification(request);
		notification = notificationPersistenceService.saveNotification(notification);
		boolean sent = sendToClient(request.getReceiverId(), notification);
		if (sent) {
			notification.markAsSent();
			notificationPersistenceService.saveNotification(notification);
		} else {
			log.error("알림이 전송되지 않았습니다.");
		}
	}

	private boolean sendToClient(Long userId, Notification notification) {
		Role role;
		if (notification.getNotificationType() == NotificationType.RESERVATION_REQUEST
			|| notification.getNotificationType() == NotificationType.RESERVATION_CANCEL) {
			role = Role.MANAGER;
		} else {
			role = Role.MEMBER;
		}

		String emitterKey = generateEmitterKey(userId, role);
		return emitterRepository.get(emitterKey).map(sseEmitter -> {
			try {
				NotificationDto.Response response = convertToResponse(notification);
				sseEmitter.send(SseEmitter.event()
					.id(notification.getNotificationId().toString())
					.name(NOTIFICATION_NAME)
					.data(response));
				return true;
			} catch (IOException exception) {
				emitterRepository.delete(emitterKey);
				log.error("Failed to send notification: {}", notification.getNotificationId(), exception);
				return false;
			}
		}).orElseGet(() -> {
			log.info("No emitter found for user: {} with NotificationType : {}", userId,
				notification.getNotificationType());
			return false;
		});
	}

	private String generateEmitterKey(Long userId, Role role) {
		return "Emitter:UID:" + userId + ":ROLE:" + role;
	}

	private Notification createNotification(NotificationDto.Request request) {

		return Notification.builder()
			.content(request.getContent())
			.senderId(request.getSenderId())
			.receiverId(request.getReceiverId())
			.notificationType(request.getNotificationType())
			.build();
	}

	public List<NotificationDto.Response.GetListResponse> getNotifications(Long userId, Role role, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<Notification> notificationPage;

		if (role == Role.MEMBER) {
			notificationPage = notificationRepository.findByMemberReceiverId(userId, pageable);
		} else {
			notificationPage = notificationRepository.findByNailArtistReceiverId(userId, pageable);
		}

		return notificationPage.getContent().stream()
			.map(this::convertToDto)
			.collect(Collectors.toList());
	}

	private NotificationDto.Response.GetListResponse convertToDto(Notification notification) {
		return NotificationDto.Response.GetListResponse.builder()
			.id(notification.getNotificationId())
			.content(notification.getContent())
			.notificationType(notification.getNotificationType())
			.createdAt(notification.getCreatedAt())
			.build();
	}

	private NotificationDto.Response convertToResponse(Notification notification) {
		NotificationDto.Response response = new NotificationDto.Response();
		response.setNotificationId(notification.getNotificationId());
		response.setContent(notification.getContent());
		response.setNotificationType(notification.getNotificationType());
		response.setSendDateTime(notification.getCreatedAt());
		response.setSenderId(notification.getSenderId());
		response.setReceiverId(notification.getReceiverId());

		return response;
	}

}
