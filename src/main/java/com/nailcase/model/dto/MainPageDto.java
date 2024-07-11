package com.nailcase.model.dto;

import java.util.List;

import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.Shop;

import lombok.Data;

@Data
public class MainPageDto {
	private List<Reservation> recentReservations;
	private List<ShopDto.MainPageResponse> topPopularShops;
	private List<Shop> likedShops;

	public MainPageDto(List<Reservation> recentReservations, List<ShopDto.MainPageResponse> topPopularShops,
		List<Shop> likedShops) {
		this.recentReservations = recentReservations;
		this.topPopularShops = topPopularShops;
		this.likedShops = likedShops;
	}

}