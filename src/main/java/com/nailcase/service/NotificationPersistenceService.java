package com.nailcase.service;

import static com.nailcase.exception.codes.NotificationErrorCode.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

	@Async
	public void saveNotification(Notification notification) {
		try {
			notificationRepository.save(notification);
			log.info("Notification successfully saved: {}", notification);
		} catch (Exception e) {
			log.error("Failed to save notification", e);
			throw new BusinessException(NOTIFICATION_CONNECTION_ERROR);
		}
	}
}

