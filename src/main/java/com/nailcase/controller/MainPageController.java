package com.nailcase.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.MainPageDto;
import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.ShopDto;
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
	public MainPageDto getMainPageData(@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails != null ? memberDetails.getId() : null;

		List<ShopDto.MainPageResponse> topPopularShops = mainPageService.getTopPopularShops(memberId);

		ReservationDto.MainPageResponse earliestReservation = null;
		List<ShopDto.MainPageResponse> likedShops = new ArrayList<>();
		List<ReservationDto.CompletedReservationResponse> recentlyCompletedReservations = new ArrayList<>();

		if (memberId != null) {
			earliestReservation = mainPageService.getEarliestReservationByMember(memberId).orElse(null);
			likedShops = mainPageService.getMemberLikedShops(memberId);
			recentlyCompletedReservations = mainPageService.getCompletedReservations(memberId);
		}

		return new MainPageDto(earliestReservation, recentlyCompletedReservations, topPopularShops, likedShops);
	}

}
