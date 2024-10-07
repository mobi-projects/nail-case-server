package com.nailcase.common;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {
	private final NotificationService notificationService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleReservationEvent(ReservationEvent event) {
		try {
			Reservation reservation = event.getReservation();
			NotificationType notificationType = event.getNotificationType();
			String content = event.getContent();

			log.info("Handling reservation event: type={}, reservationId={}", notificationType,
				reservation.getReservationId());

			Long senderId, receiverId;

			if (notificationType == NotificationType.RESERVATION_REQUEST
				|| notificationType == NotificationType.RESERVATION_CANCEL) {
				senderId = reservation.getCustomer().getMemberId();
				receiverId = Optional.ofNullable(reservation.getNailArtist())
					.map(NailArtist::getNailArtistId)
					.orElseThrow(() -> new IllegalStateException(
						"NailArtist is null for reservation: " + reservation.getReservationId()));
			} else {
				senderId = Optional.ofNullable(reservation.getNailArtist())
					.map(NailArtist::getNailArtistId)
					.orElseThrow(() -> new IllegalStateException(
						"NailArtist is null for reservation: " + reservation.getReservationId()));
				receiverId = reservation.getCustomer().getMemberId();

			}

			NotificationDto.Request notificationRequest = new NotificationDto.Request();
			notificationRequest.setSenderId(senderId);
			notificationRequest.setReceiverId(receiverId);
			notificationRequest.setContent(content);
			notificationRequest.setNotificationType(notificationType);

			log.info("Sending notification: sender={}, receiver={}, type={}", senderId, receiverId, notificationType);
			notificationService.sendNotificationToClient(notificationRequest);
		} catch (Exception e) {
			log.error("Error processing reservation event", e);
			// 여기서 예외를 다시 던지지 않음으로써 예약 트랜잭션에는 영향을 주지 않습니다.
		}
	}
}