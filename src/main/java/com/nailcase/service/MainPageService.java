package com.nailcase.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.ReservationDto;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainPageService {
	private final ShopService shopService;
	private final ReservationService reservationService;
	private final MemberRepository memberRepository;
	private final ShopMapper shopMapper;

	// 사용자 ID를 기반으로 사용자의 최근 예약 목록 가져오기
	public Optional<ReservationDto.MainPageResponse> getEarliestReservationByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		ReservationDto.MainPageResponse earliestReservation = reservationService.findEarliestReservationByCustomer(
			member);
		return Optional.ofNullable(earliestReservation);
	}

	// 인기 매장 목록 가져오기, 상위 3개 매장만 반환
	@Transactional(readOnly = true)
	public List<ShopDto.MainPageResponse> getTopPopularShops() {
		Pageable topThree = PageRequest.of(0, 3, Sort.by("likes").descending());
		return shopService.findTopPopularShops(topThree).map(shopMapper::toMainPageResponse).getContent();
	}

	// 사용자가 좋아요한 매장 목록 가져오기
	@Transactional(readOnly = true)
	public List<ShopDto.MainPageResponse> getMemberLikedShops(Long memberId) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<Shop> likedShopsPage = shopService.findLikedShopsByMemberInMainPage(memberId, pageable);

		// Shop 엔티티를 ShopDto.MainPageResponse로 변환
		return likedShopsPage.getContent().stream()
			.map(shopMapper::toMainPageResponse)
			.collect(Collectors.toList());
	}
}