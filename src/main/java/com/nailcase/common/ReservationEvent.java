package com.nailcase.common;

import java.time.LocalDateTime;

import com.nailcase.model.entity.Reservation;
import com.nailcase.model.enums.NotificationType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationEvent {

	private final Reservation reservation;
	private final NotificationType notificationType;
	private String nickname;
	private final String content;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;

}
