package com.nailcase.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.entity.Member;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopLikedMemberRepository;
import com.nailcase.repository.ShopRepository;

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

		System.out.println("response = " + response);
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

	public ShopDto.InfiniteScrollResponse getTopPopularShops(UserPrincipal userPrincipal, Pageable pageable) {
		// 1. 인기순으로 정렬된 페이지 요청 생성
		if (userPrincipal.role() == com.nailcase.model.enums.Role.MANAGER) {
			throw new BusinessException(UserErrorCode.ONLY_MEMBER_PAGE);
		}
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
			Sort.by("likes").descending());

		// 2. 정렬된 shop 목록 조회
		Long memberId = userPrincipal.id();
		Page<ShopDto.MainPageResponse> topPopularShops = shopRepository.getTopPopularShops(memberId, sortedPageable);

		// 5. InfiniteScrollResponse 생성 및 반환
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