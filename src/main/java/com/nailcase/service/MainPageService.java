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
import com.nailcase.model.entity.Member;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopLikedMemberRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.util.StringUtils;
import com.querydsl.core.Tuple;

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
		// 1. 인기순으로 정렬된 페이지 요청 생성
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
			Sort.by("likes").descending());

		// 2. 정렬된 shop 목록 조회 (raw 데이터)
		Page<Tuple> topPopularShopsRaw = shopRepository.getTopPopularShops(memberId, sortedPageable);

		// 3. raw 데이터를 MainPageResponse로 변환
		Page<ShopDto.MainPageResponse> topPopularShops = topPopularShopsRaw.map(tuple -> {
			Long shopId = tuple.get(0, Long.class);
			String shopName = tuple.get(1, String.class);
			String bucketName = tuple.get(2, String.class);
			String objectName = tuple.get(3, String.class);
			Boolean likedByUser = tuple.get(4, Boolean.class);

			String shopImageUrl = null;
			if (bucketName != null && objectName != null) {
				shopImageUrl = StringUtils.generateImageUrl(bucketName, objectName);
			}

			return ShopDto.MainPageResponse.builder()
				.shopId(shopId)
				.shopName(shopName)
				.shopImageUrl(shopImageUrl)
				.likedByUser(likedByUser != null && likedByUser)
				.build();
		});

		// 4. InfiniteScrollResponse 생성 및 반환
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