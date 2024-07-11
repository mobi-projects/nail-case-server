package com.nailcase.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.service.ReservationFacade;
import com.nailcase.service.ReservationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/reservations")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;
	private final ReservationFacade reservationFacade;

	@PostMapping
	public ReservationDto.Response registerReservation(
		@PathVariable Long shopId,
		@RequestBody ReservationDto.Post dto,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return reservationFacade.createReservation(shopId, memberDetails.getMemberId(), dto);
	}

	// TODO: 어디까지 변경 가능한지 알아야 함
	@PatchMapping("/{reservationId}")
	public ReservationDto.Response updateReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@RequestBody ReservationDto.Patch dto,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return reservationService.updateReservation(shopId, reservationId, memberDetails.getMemberId(), dto);
	}

	@GetMapping
	public List<ReservationDto.Response> listReservation(
		@PathVariable Long shopId,
		@RequestParam Long startTime,
		@RequestParam Long endTime
	) {
		// TODO: 예약 조회할때 shop 에 권한있는 사람일 때 목록 조회 가능
		// TODO: 예약을 위해 예약 조회할때에는 정보가 간소화 되어야 함
		return reservationService.listReservation(shopId, startTime, endTime);
	}

	@GetMapping("/{reservationId}")
	public ReservationDto.Response viewReservation(@PathVariable Long shopId, @PathVariable Long reservationId) {
		return reservationService.viewReservation(shopId, reservationId);
	}

	@GetMapping("/time")
	public List<ReservationDto.Available> listAvailableTime(
		@PathVariable Long shopId,
		@RequestParam Long[] artistIds,
		@RequestParam Long date
	) {
		return reservationFacade.listAvailableTime(shopId, artistIds, date);
	}
}
