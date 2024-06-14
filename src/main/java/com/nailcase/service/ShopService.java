package com.nailcase.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.ShopRegisterDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
	private final ShopMapper shopMapper = ShopMapper.INSTANCE;
	private final ShopRepository shopRepository;
	private final MemberRepository memberRepository;
	private final ShopInfoService shopInfoService;
	private final ShopHourService shopHourService;

	@Transactional
	public ShopRegisterDto.Response registerShop(
		ShopRegisterDto.Request shopRegisterRequest,
		Long memberId
	) throws BusinessException {
		// 이 친구가 있어야함
		// Set member role MANAGER
		Member member = memberRepository
			.findById(memberId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

		member.setRole(Role.MANAGER);

		// Create shop
		Shop shop = shopMapper.toEntity(shopRegisterRequest);
		shop.setOwnerId(memberId);

		Shop savedShop = shopRepository.save(shop);

		// Create shop info, shop hour init
		shopInfoService.initShopInfo(memberId);
		shopHourService.initShopHour(memberId);

		return shopMapper.toResponse(savedShop);
	}
}
