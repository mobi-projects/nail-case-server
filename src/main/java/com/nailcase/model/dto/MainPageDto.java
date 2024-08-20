package com.nailcase.model.dto;

import lombok.Data;

@Data
public class MainPageDto {
	private ReservationDto.MainPageResponse recentReservation;

	public MainPageDto(ReservationDto.MainPageResponse recentReservation) {
		this.recentReservation = recentReservation;
	}
}