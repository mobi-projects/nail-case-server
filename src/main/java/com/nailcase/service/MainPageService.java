package com.nailcase.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Member;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopLikedMemberRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MainPageService {
	private final ShopService shopService;
	private final ReservationService reservationService;
	private final ShopRepository shopRepository;
	private final MemberRepository memberRepository;
	private final ShopLikedMemberRepository shopLikedMemberRepository;

	// 사용자 ID를 기반으로 사용자의 최근 예약 가져오기 -> 아직 시술받지 않은 예약만
	public Optional<ReservationDto.MainPageResponse> getEarliestReservationByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		ReservationDto.MainPageResponse response = reservationService.findEarliestReservationByCustomer(member);

		if (response == null || response.getDetails() == null || response.getDetails().isEmpty()) {
			return Optional.empty();
		}

		boolean hasValidStartTimes = response.getDetails().stream()
			.allMatch(detail -> detail.getStartTime() != null);

		return hasValidStartTimes ? Optional.of(response) : Optional.empty();
	}

	// 사용자 ID를 기반으로 사용자의 과거 예약 가져오기 -> 시술을 받은 예약들만, 최대3개
	public List<ReservationDto.CompletedReservationResponse> getCompletedReservations(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return reservationService.findRecentlyCompletedReservationByCustomer(member);
	}

	public ShopDto.InfiniteScrollResponse getTopPopularShops(Optional<Long> memberId, Pageable pageable) {
		Page<ShopDto.MainPageBeforeResponse> topPopularShopsBefore = shopRepository.getTopPopularShops(memberId,
			pageable);

		// ShopDto.MainPageBeforeResponse 내부에서 shopImageUrl 생성 로직 처리
		List<ShopDto.MainPageResponse> convertedShopList = topPopularShopsBefore.getContent().stream()
			.map(beforeResponse -> ShopDto.MainPageResponse.builder()
				.shopId(beforeResponse.getShopId())
				.shopName(beforeResponse.getShopName())
				.shopImageUrl(
					StringUtils.generateImageUrl(beforeResponse.getBucketName(), beforeResponse.getObjectName()))
				.likedByUser(beforeResponse.isLikedByUser())
				.build())
			.toList();
		// ShopDto.MainPageResponse 내부에서 shopImageUrl 생성 로직 처리
		Page<ShopDto.MainPageResponse> topPopularShops = new PageImpl<>(
			convertedShopList,
			topPopularShopsBefore.getPageable(),
			topPopularShopsBefore.getTotalElements()
		);

		return ShopDto.InfiniteScrollResponse.builder()
			.shopList(topPopularShops.getContent())
			.last(topPopularShops.isLast())
			.pageNumber(topPopularShops.getNumber())
			.pageSize(topPopularShops.getSize())
			.totalElements(topPopularShops.getTotalElements())
			.totalPages(topPopularShops.getTotalPages())
			.build();
	}

}