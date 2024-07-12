package com.nailcase.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.WorkHour;

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
		nailArtistService.verifyArtistsExistenceInShop(Arrays.asList(nailArtistIds), shop.getNailArtists());
		WorkHour workHour = workHourService.verifyBusinessDay(shop.getWorkHours(), date);
		return reservationService.listAvailableTime(shop, nailArtistIds, workHour, date);
	}

	@Transactional
	public ReservationDto.Response createReservation(Long shopId, Long memberId, ReservationDto.Post dto) {
		Shop shop = shopService.findByShopIdAndNailArtistsAndWorkHours(shopId);
		nailArtistService.verifyArtistsExistenceInShop(dto.getNailArtistIds(), shop.getNailArtists());
		workHourService.verifyTimeInOpeningHour(shop.getWorkHours(), dto.getStartTime());
		return reservationService.createReservation(shopId, memberId, dto);
	}
}
