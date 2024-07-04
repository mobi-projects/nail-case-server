package com.nailcase.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ShopInfoErrorCode;
import com.nailcase.mapper.ShopInfoMapper;
import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.repository.ShopInfoRepository;
import com.nailcase.repository.ShopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopInfoService {
	private final ShopInfoMapper shopInfoMapper = ShopInfoMapper.INSTANCE;
	private final ShopInfoRepository shopInfoRepository;
	private final ShopRepository shopRepository;
	private final ShopService shopService;

	@Transactional(readOnly = true)
	protected ShopInfo getShopInfoByShopId(Long shopId) throws BusinessException {
		return shopInfoRepository
			.findByShopId(shopId)
			.orElseThrow(() -> new BusinessException(ShopInfoErrorCode.SHOP_INFO_NOT_FOUND));
	}

	@Transactional
	public ShopInfoDto.Address updateAddress(
		Long shopId,
		ShopInfoDto.Address requestAddress,
		Long memberId
	) throws BusinessException {

		// TODO 권한검사
		log.debug(String.valueOf(memberId));

		Shop shop = shopService.getShopById(shopId);
		ShopInfo shopInfo = getShopInfoByShopId(shopId);

		shop.setAddress(requestAddress.getAddress());
		shopInfo.setPoint(requestAddress.getPoint());

		Shop updatedShop = shopRepository.saveAndFlush(shop);
		ShopInfo updatedShopInfo = shopInfoRepository.saveAndFlush(shopInfo);

		return shopInfoMapper.toAddressResponse(updatedShop.getAddress(), updatedShopInfo.getPoint());
	}
}
