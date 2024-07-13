package com.nailcase.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class MainPageDto {
	private ReservationDto.MainPageResponse recentReservation;
	private List<ShopDto.MainPageResponse> topPopularShops;
	private List<ShopDto.MainPageResponse> likedShops;
	private List<ReservationDto.CompletedReservationResponse> max3RecentlyCompletedReservation;

	public MainPageDto(ReservationDto.MainPageResponse recentReservation,
		List<ReservationDto.CompletedReservationResponse> max3RecentlyCompletedReservation,
		List<ShopDto.MainPageResponse> topPopularShops,
		List<ShopDto.MainPageResponse> likedShops) {
		this.recentReservation = recentReservation;
		this.max3RecentlyCompletedReservation = max3RecentlyCompletedReservation;
		this.topPopularShops = topPopularShops;
		this.likedShops = likedShops;
	}
}