package com.nailcase.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class MainPageDto {
	private ReservationDto.MainPageResponse recentReservation;
	private List<ShopDto.MainPageResponse> topPopularShops;
	private List<ShopDto.MainPageResponse> likedShops;  // List<Shop>에서 변경

	public MainPageDto(ReservationDto.MainPageResponse recentReservation,
		List<ShopDto.MainPageResponse> topPopularShops,
		List<ShopDto.MainPageResponse> likedShops) {
		this.recentReservation = recentReservation;
		this.topPopularShops = topPopularShops;
		this.likedShops = likedShops;
	}

}