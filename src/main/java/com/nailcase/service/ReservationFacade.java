package com.nailcase.service;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.model.enums.ReservationStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationFacade {

	private final ShopService shopService;
	private final NailArtistService nailArtistService;
	private final WorkHourService workHourService;
	private final ReservationService reservationService;

	public List<ReservationDto.Available> listAvailableTime(Long shopId, Long[] nailArtistIds, Long date) {
		Shop shop = shopService.findByShopIdAndNailArtistsAndWorkHours(shopId);
		WorkHour workHour = workHourService.verifyBusinessDay(shop.getWorkHours(), date);
		return reservationService.listAvailableTime(shop, nailArtistIds, workHour, date);
	}

	@Transactional
	public ReservationDto.RegisterResponse createReservation(Long shopId, Long memberId, ReservationDto.Post dto) {
		Shop shop = shopService.findByShopIdAndNailArtistsAndWorkHours(shopId);
		workHourService.verifyTimeInOpeningHour(shop.getWorkHours(), dto.getStartTime());
		return reservationService.createReservation(shopId, memberId, dto);
	}

	@Transactional
	public ReservationDto.Response updateReservationStatus(Long shopId, Long reservationId, Long memberId,
		ReservationStatus status) {
		Shop shop = shopService.getShopById(shopId);

		return reservationService.updateReservationStatus(shop, reservationId, memberId, status);
	}

	@Transactional
	public ReservationDto.Response updateReservationWitchReject(Long shopId, Long reservationId, Long memberId,
		ReservationStatus status, ReservationDto.RejectReasonRequest cancelReasonRequest) {
		Shop shop = shopService.getShopById(shopId);

		return reservationService.updateReservationStatus(shop, reservationId, memberId, status,
			cancelReasonRequest.getRejectReason());
	}

	@Transactional
	public ReservationDto.Response confirmReservation(Long shopId, Long reservationId,
		@AuthenticationPrincipal Long userId,
		ReservationDto.Confirm dto) {
		Shop shop = shopService.getShopById(shopId);
		return reservationService.confirmReservation(shop, reservationId, userId, dto);
	}
}
