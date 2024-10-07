package com.nailcase.common;

import com.nailcase.model.entity.Reservation;
import com.nailcase.model.enums.NotificationType;

import lombok.Getter;

@Getter
public class ReservationEvent {

	private final Reservation reservation;
	private final NotificationType notificationType;
	private final String content;

	public ReservationEvent(Reservation reservation, NotificationType notificationType, String content) {
		this.reservation = reservation;
		this.notificationType = notificationType;
		this.content = content;
	}

}
