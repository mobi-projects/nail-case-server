package com.nailcase.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopLikedMemberRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MainPageService {
	private final ShopService shopService;
	private final ReservationService reservationService;
	private final MemberRepository memberRepository;
	private final ShopLikedMemberRepository shopLikedMemberRepository;

	// 사용자 ID를 기반으로 사용자의 최근 예약 가져오기 -> 아직 시술받지 않은 예약만
	public Optional<ReservationDto.MainPageResponse> getEarliestReservationByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return Optional.ofNullable(reservationService.findEarliestReservationByCustomer(member));
	}

	// 사용자 ID를 기반으로 사용자의 과거 예약 가져오기 -> 시술을 받은 예약들만, 최대3개
	public List<ReservationDto.CompletedReservationResponse> getCompletedReservations(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return reservationService.findRecentlyCompletedReservationByCustomer(member);
	}

	// 인기 매장 목록 가져오기, 상위 3개 매장만 반환
	public List<ShopDto.MainPageResponse> getTopPopularShops(Long memberId) {
		Pageable topThree = PageRequest.of(0, 3, Sort.by("likes").descending());
		List<Shop> shops = shopService.findTopPopularShops(topThree);
		Set<Long> likedShopIds = shopLikedMemberRepository.findLikedShopIdsByMemberId(memberId);
		return getShopMainPageResponses(shops, likedShopIds);
	}

	// 자기가 좋아한 샵 목록 가져오기
	// MainPageService.java
	public List<ShopDto.MainPageResponse> getMemberLikedShops(Long memberId) {
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
		List<Shop> shops = shopService.findMemberLikedShops(memberId, pageable);
		Set<Long> likedShopIds = shopLikedMemberRepository.findLikedShopIdsByMemberId(memberId); // 사용자가 좋아한 매장 ID 가져오기
		return getShopMainPageResponses(shops, likedShopIds);
	}

	@NotNull
	private List<ShopDto.MainPageResponse> getShopMainPageResponses(List<Shop> shops, Set<Long> likedShopIds) {
		return shops.stream()
			.map(shop -> {
				ShopDto.MainPageResponse response = new ShopDto.MainPageResponse();
				response.setId(shop.getShopId());
				response.setName(shop.getShopName());
				response.setOverview(shop.getOverview());
				response.setLikedByUser(likedShopIds.contains(shop.getShopId()));
				return response;
			})
			.collect(Collectors.toList());
	}

}