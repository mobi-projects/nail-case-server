package com.nailcase.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.MainPageDto;
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
	public MainPageDto getMainPageData(@AuthenticationPrincipal Long userId) {

		ReservationDto.MainPageResponse earliestReservation;

		if (userId != null) {
			earliestReservation = mainPageService.getEarliestReservationByMember(userId)
				.filter(reservation -> reservation.getDetails() != null
					&& !reservation.getDetails().isEmpty()
					&& reservation.getDetails()
					.stream()
					.allMatch(detail -> detail.getStartTime() != null))
				.orElse(null);
			return new MainPageDto(earliestReservation);
		} else {
			return new MainPageDto(null);
		}
	}

	@GetMapping("/shopsList")
	public ShopDto.InfiniteScrollResponse getTopPopularShops(
		@AuthenticationPrincipal(expression = "principal?.id") Long memberId,
		Pageable pageable) {
		return mainPageService.getTopPopularShops(Optional.ofNullable(memberId), pageable);
	}

}
