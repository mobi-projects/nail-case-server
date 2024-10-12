package com.nailcase.config.batch.hourlyPending;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.dto.ReservationDetailProjection;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationItemProcessor
	implements ItemProcessor<ReservationDetailProjection, ReservationDetailProjection> {
	private final NotificationService notificationService;

	@Override
	public ReservationDetailProjection process(ReservationDetailProjection projection) throws Exception {
		NotificationDto.Request request = NotificationDto.Request.builder()
			.nickname(projection.getShopName())
			.content("에서 예약을 거절하였습니다.")
			.senderId(null) // 시스템 알림으로 처리
			.receiverId(projection.getCustomerId())
			.notificationType(NotificationType.RESERVATION_REJECT)
			.reservationId(projection.getReservationDetailId())
			.build();

		notificationService.sendNotificationToClient(request);

		return projection;
	}
}
