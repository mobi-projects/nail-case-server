package com.nailcase.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Reservation;
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
	@Transactional(readOnly = true)
	public List<Reservation> getRecentReservationsByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return reservationService.findReservationsByCustomer(member);
	}

	// 인기 매장 목록 가져오기, 상위 3개 매장만 반환
	@Transactional(readOnly = true)
	public List<ShopDto.Response> getTopPopularShops() {
		Pageable topThree = PageRequest.of(0, 3, Sort.by("likes").descending());
		return shopService.findTopPopularShops(topThree).map(shopMapper::toResponse).getContent();
	}

	// 사용자가 좋아요한 매장 목록 가져오기
	@Transactional(readOnly = true)
	public List<Shop> getMemberLikedShops(Long memberId) {
		memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		// 좋아요한 매장 3개만 가져오기 위해 PageRequest를 생성합니다.
		// 여기서는 최신 순으로 정렬하고자 할 때 'createdAt' 필드를 기준으로 정렬합니다.
		Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

		// 서비스 계층에 pageable 객체를 전달받은 데이터
		Page<Shop> likedShopsPage = shopService.findLikedShopsByMemberInMainPage(memberId, pageable);

		// Page 객체에서 실제 List<Shop>을 추출하여 반환
		return likedShopsPage.getContent();
	}
}