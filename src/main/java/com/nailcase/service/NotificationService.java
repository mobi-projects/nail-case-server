package com.nailcase.service;

import static com.nailcase.exception.codes.CommonErrorCode.*;
import static com.nailcase.exception.codes.NotificationErrorCode.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.nailcase.exception.BusinessException;
import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Notification;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.EmitterRepository;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;
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
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;

	public SseEmitter connectNotification(Long userId, Role role) {
		SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
		emitterRepository.save(userId, sseEmitter);

		sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
		sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

		try {
			sseEmitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("Connection completed"));
		} catch (IOException exception) {
			emitterRepository.delete(userId);
			throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
		}
		return sseEmitter;
	}

	public void sendNotification(NotificationDto.Request request) {
		Notification notification = createNotification(request);
		notificationRepository.save(notification);

		Long receiverId = getReceiverId(notification);
		sendToClient(receiverId, notification.getNotificationId());
	}

	private Long getReceiverId(Notification notification) {
		return Optional.ofNullable(notification.getReceiverMember())
			.map(Member::getMemberId)
			.orElseGet(() -> Optional.ofNullable(notification.getReceiverNailArtist())
				.map(NailArtist::getNailArtistId)
				.orElseThrow(() -> new BusinessException(RECEIVER_NOT_FOUND)));
	}

	private void sendToClient(Long userId, Long notificationId) {
		emitterRepository.get(userId).ifPresentOrElse(
			sseEmitter -> {
				try {
					sseEmitter.send(SseEmitter.event()
						.id(notificationId.toString())
						.name(NOTIFICATION_NAME)
						.data("New notification"));
				} catch (IOException exception) {
					emitterRepository.delete(userId);
					throw new BusinessException(NOTIFICATION_SEND_ERROR);
				}
			},
			() -> log.info("No emitter found for user: {}", userId)
		);
	}

	private Notification createNotification(NotificationDto.Request request) {
		Notification notification = Notification.builder()
			.content(request.getContent())
			.notificationType(request.getNotificationType())
			.build();

		setSender(notification, request.getSenderId(), request.getSenderType());
		setReceiver(notification, request.getReceiverId(), request.getReceiverType());

		return notification;
	}

	private void setSender(Notification notification, Long senderId, Role senderType) {
		if (senderType == Role.MEMBER) {
			Member sender = memberRepository.findById(senderId)
				.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
			notification.updateSender(sender);
		} else {
			NailArtist sender = nailArtistRepository.findById(senderId)
				.orElseThrow(() -> new BusinessException(NAIL_ARTIST_NOT_FOUND));
			notification.updateSender(sender);
		}
	}

	private void setReceiver(Notification notification, Long receiverId, Role receiverType) {
		if (receiverType == Role.MEMBER) {
			Member receiver = memberRepository.findById(receiverId)
				.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
			notification.updateReceiver(receiver);
		} else {
			NailArtist receiver = nailArtistRepository.findById(receiverId)
				.orElseThrow(() -> new BusinessException(NAIL_ARTIST_NOT_FOUND));
			notification.updateReceiver(receiver);
		}
	}

	public List<NotificationDto.Response.GetListResponse> getNotifications(Long userId, Role role, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<Notification> notificationPage;

		if (role == Role.MEMBER) {
			notificationPage = notificationRepository.findByReceiverMember_MemberId(userId, pageable);
		} else {
			notificationPage = notificationRepository.findByReceiverNailArtist_NailArtistId(userId, pageable);
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
}
