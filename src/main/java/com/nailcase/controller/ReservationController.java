package com.nailcase.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(ReservationStatus.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				setValue(ReservationStatus.valueOf(text.toUpperCase()));
			}
		});
	}

	private final ReservationService reservationService;
	private final ReservationFacade reservationFacade;

	@PostMapping
	public ReservationDto.RegisterResponse registerReservation(
		@PathVariable Long shopId,
		@RequestBody ReservationDto.Post dto,
		@AuthenticationPrincipal Long userId
	) {
		return reservationFacade.createReservation(shopId, userId, dto);
	}

	@PatchMapping("/{reservationId}/cancel")
	public ReservationDto.Response cancelReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@AuthenticationPrincipal Long userId
	) {
		return reservationFacade.updateReservationStatus(shopId, reservationId, userId,
			ReservationStatus.CANCELED);
	}

	@PatchMapping("/{reservationId}/reject")
	public ReservationDto.Response rejectReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@AuthenticationPrincipal Long userId,
		@RequestBody ReservationDto.RejectReasonRequest request
	) {
		return reservationFacade.updateReservationWitchReject(shopId, reservationId, userId,
			ReservationStatus.REJECTED, request);
	}

	@PatchMapping("/{reservationId}/complete")
	public ReservationDto.Response completeReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@AuthenticationPrincipal Long userId
	) {

		return reservationFacade.updateReservationStatus(shopId, reservationId, userId,
			ReservationStatus.COMPLETED);
	}

	@PatchMapping("/{reservationId}/confirm")
	public ReservationDto.Response confirmReservation(
		@PathVariable Long shopId,
		@PathVariable Long reservationId,
		@RequestBody ReservationDto.Confirm request,
		@AuthenticationPrincipal Long userId
	) {
		return reservationFacade.confirmReservation(shopId, reservationId, userId,
			request);
	}

	//TODO MANAGER
	@GetMapping
	public ReservationDto.pageableResponse listReservation(
		@PathVariable Long shopId,
		@RequestParam(required = false) Long startDate,
		@RequestParam(required = false) Long endDate,
		@RequestParam(required = false) ReservationStatus status,
		Pageable pageable) {
		// TODO: 예약 조회할때 shop 에 권한있는 사람일 때 목록 조회 가능
		// TODO: 예약을 위해 예약 조회할때에는 정보가 간소화 되어야 함
		return reservationService.listReservation(shopId, startDate, endDate, status, pageable);
	}

	@GetMapping("/{reservationId}")
	public ReservationDto.viewResponse viewReservation(@PathVariable Long shopId, @PathVariable Long reservationId) {
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
