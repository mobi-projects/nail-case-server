package com.nailcase.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.ReservationDto;
import com.nailcase.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/reservations")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping
	public ReservationDto.Response registerReservation(
		@PathVariable Long shopId,
		@RequestBody ReservationDto.Post dto
	) {
		return reservationService.createReservation(shopId, dto);
	}

	// TODO: 어디까지 변경 가능한지 알아야 함
	@PatchMapping("/{reservationId}")
	public ReservationDto.Response updateReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@RequestBody ReservationDto.Patch dto
	) {
		return reservationService.updateReservation(shopId, reservationId, dto);
	}

	@GetMapping
	public List<ReservationDto.Response> listReservation(
		@PathVariable Long shopId,
		@RequestParam Long startTime,
		@RequestParam Long endTime
	) {
		return reservationService.listReservation(shopId, startTime, endTime);
	}

	@GetMapping("/{reservationId}")
	public ReservationDto.Response viewReservation(@PathVariable Long shopId, @PathVariable Long reservationId) {
		return reservationService.viewReservation(shopId, reservationId);
	}
}
