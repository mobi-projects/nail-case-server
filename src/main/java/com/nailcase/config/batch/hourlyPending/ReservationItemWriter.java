package com.nailcase.config.batch.hourlyPending;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.nailcase.model.dto.ReservationDetailProjection;
import com.nailcase.model.entity.Notification;
import com.nailcase.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationItemWriter implements ItemWriter<ReservationDetailProjection> {

	private final NotificationRepository notificationRepository;

	@Override
	public void write(Chunk<? extends ReservationDetailProjection> chunk) throws Exception {
		for (ReservationDetailProjection projection : chunk) {
			Notification notification = notificationRepository.findByReservationDetailId(
				projection.getReservationDetailId()).orElse(null);
			if (notification != null) {
				notification.updateSent();
				notificationRepository.save(notification);
				log.info("Updated notification sent status for reservation: {}", projection.getReservationDetailId());
			} else {
				log.warn("No notification found for reservation: {}", projection.getReservationDetailId());
			}
		}
	}
}
