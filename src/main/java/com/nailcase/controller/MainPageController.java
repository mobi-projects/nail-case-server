package com.nailcase.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.MainPageDto;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.Shop;
import com.nailcase.service.MainPageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainPageController {
	private final MainPageService mainPageService;

	@GetMapping
	public MainPageDto getMainPageData(@RequestParam(required = false) Long memberId) {
		List<ShopDto.MainPageResponse> topPopularShops = mainPageService.getTopPopularShops();
		List<Shop> likedShops = List.of(); // 비어있는 리스트로 초기화
		List<Reservation> recentReservations = List.of(); // 비어있는 리스트로 초기화

		if (memberId != null) {
			recentReservations = mainPageService.getRecentReservationsByMember(memberId);
			likedShops = mainPageService.getMemberLikedShops(memberId);
		}

		return new MainPageDto(recentReservations, topPopularShops, likedShops);
	}
}
