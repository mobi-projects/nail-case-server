package com.nailcase.common;

import static com.nailcase.exception.codes.ReservationErrorCode.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nailcase.exception.BusinessException;
import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.repository.ReservationRepository;
import com.nailcase.service.NotificationService;
import com.nailcase.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {
	private final NotificationService notificationService;
	private final ReservationRepository reservationRepository;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleReservationEvent(ReservationEvent event) {
		try {
			Reservation reservation = reservationRepository.findReservationWithDetailsById(
					event.getReservation().getReservationId())
				.orElseThrow(() -> new BusinessException(RESERVATION_NOT_FOUND));
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

			NotificationDto.Request notificationRequest = NotificationDto.Request.builder()
				.reservationId(reservation.getReservationId())
				.senderId(senderId)
				.receiverId(receiverId)
				.nickname(event.getNickname())
				.content(content)
				.notificationType(notificationType)
				.sendDateTime(DateUtils.localDateTimeToUnixTimeStampWithNull(LocalDateTime.now()))
				.startTime(DateUtils.localDateTimeToUnixTimeStampWithNull(event.getStartTime()))
				.endTime(DateUtils.localDateTimeToUnixTimeStampWithNull(event.getEndTime()))
				.build();

			log.info("Sending notification: sender={}, receiver={}, type={}", senderId, receiverId, notificationType);
			notificationService.sendNotificationToClient(notificationRequest);
		} catch (Exception e) {
			log.error("Error processing reservation event", e);
			// 여기서 예외를 다시 던지지 않음으로써 예약 트랜잭션에는 영향을 주지 않습니다.
		}
	}
}