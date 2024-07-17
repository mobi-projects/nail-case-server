package com.nailcase.controller;

import java.time.LocalDateTime;
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
import com.nailcase.model.dto.NailArtistDetails;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.enums.ReservationStatus;
import com.nailcase.service.ReservationFacade;
import com.nailcase.service.ReservationService;
import com.nailcase.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/shops/{shopId}/reservations")
@RequiredArgsConstructor
@Slf4j
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

	@PatchMapping("/{reservationId}/cancel")
	public ReservationDto.Response cancelReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return reservationFacade.updateReservationStatus(shopId, reservationId, memberDetails.getMemberId(),
			ReservationStatus.CANCELED);
	}

	@PatchMapping("/{reservationId}/reject")
	public ReservationDto.Response rejectReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails
	) {
		return reservationFacade.updateReservationStatus(shopId, reservationId, nailArtistDetails.getNailArtistId(),
			ReservationStatus.REJECTED);
	}

	@PatchMapping("/{reservationId}/complete")
	public ReservationDto.Response completeReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails
	) {
		Long nailArtistId = nailArtistDetails.getNailArtistId();
		return reservationFacade.updateReservationStatus(shopId, reservationId, nailArtistDetails.getNailArtistId(),
			ReservationStatus.COMPLETED);
	}

	@PatchMapping("/{reservationId}/confirm")
	public ReservationDto.Response confirmReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@RequestBody ReservationDto.Confirm request,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails
	) {
		return reservationFacade.confirmReservation(shopId, reservationId, nailArtistDetails.getNailArtistId(),
			request);
	}

	//TODO MANAGER
	@GetMapping
	public List<ReservationDto.Response> listReservation(
		@PathVariable Long shopId,
		@RequestParam(required = false) Long startDate,
		@RequestParam(required = false) Long endDate,
		@RequestParam(required = false, defaultValue = "CONFIRMED") ReservationStatus status
	) {
		// TODO: 예약 조회할때 shop 에 권한있는 사람일 때 목록 조회 가능
		// TODO: 예약을 위해 예약 조회할때에는 정보가 간소화 되어야 함
		return reservationService.listReservation(shopId, startDate, endDate, status);
	}

	@GetMapping("/{reservationId}")
	public ReservationDto.Response viewReservation(@PathVariable Long shopId, @PathVariable Long reservationId) {
		return reservationService.viewReservation(shopId, reservationId);
	}

	@GetMapping("/time")
	public List<ReservationDto.Available> listAvailableTime(
		@PathVariable Long shopId,
		@RequestParam(required = false) Long[] artistIds,
		@RequestParam Long date
	) {
		LocalDateTime localDateTime = DateUtils.unixTimeStampToLocalDateTime(date);
		log.info("localDateTime: {}", localDateTime);
		return reservationFacade.listAvailableTime(shopId, artistIds, date);
	}
}
