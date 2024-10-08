package com.nailcase.service;

import static com.nailcase.exception.codes.NotificationErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.model.entity.Notification;
import com.nailcase.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPersistenceService {
	private final NotificationRepository notificationRepository;

	@Transactional
	public Notification saveNotification(Notification notification) {
		try {
			log.info("Saving notification: {}", notification);
			Notification savedNotification = notificationRepository.save(notification);
			notificationRepository.flush();  // 추가
			log.info("Notification successfully saved with ID: {}", savedNotification.getNotificationId());
			log.info("Saved notification: {}", savedNotification);
			return savedNotification;
		} catch (Exception e) {
			log.error("Failed to save notification: {}", notification, e);
			throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
		}
	}
}

