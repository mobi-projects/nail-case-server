package com.nailcase.model.dto;

import java.util.List;

import com.nailcase.model.entity.Shop;

import lombok.Data;

@Data
public class MainPageDto {
	private ReservationDto.MainPageResponse recentReservation;
	private List<ShopDto.MainPageResponse> topPopularShops;
	private List<Shop> likedShops;

	public MainPageDto(ReservationDto.MainPageResponse recentReservation,
		List<ShopDto.MainPageResponse> topPopularShops,
		List<Shop> likedShops) {
		this.recentReservation = recentReservation;
		this.topPopularShops = topPopularShops;
		this.likedShops = likedShops;
	}

}