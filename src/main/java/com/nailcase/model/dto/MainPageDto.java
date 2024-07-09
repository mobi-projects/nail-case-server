package com.nailcase.model.dto;

import java.util.List;

import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.Shop;

import lombok.Data;

@Data
public class MainPageDto {
	private List<Reservation> recentReservations;
	private List<ShopDto.Response> topPopularShops;
	private List<Shop> likedShops;

	public MainPageDto(List<Reservation> recentReservations, List<ShopDto.Response> topPopularShops,
		List<Shop> likedShops) {
		this.recentReservations = recentReservations;
		this.topPopularShops = topPopularShops;
		this.likedShops = likedShops;
	}

}