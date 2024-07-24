package com.nailcase.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.mapper.ShopInfoMapper;
import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.PriceImage;
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
	private final PriceImageService priceImageService;

	@Transactional(readOnly = true)
	protected ShopInfo getShopInfoByShopId(Long shopId) throws BusinessException {
		return shopService.getShopInfoByShopId(shopId);
	}

	@Transactional(readOnly = true)
	public ShopInfoDto.Response getShopInfo(Long shopId) throws BusinessException {
		return shopInfoMapper.toResponse(getShopInfoByShopId(shopId));
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

	@Transactional
	public ShopInfoDto.Info updateInfo(
		Long shopId,
		ShopInfoDto.Info requestInfo,
		Long memberId
	) throws BusinessException {

		// TODO 권한 검사
		log.debug(String.valueOf(memberId));

		ShopInfo shopInfo = getShopInfoByShopId(shopId);

		shopInfo.setParkingLotCnt(requestInfo.getParkingLotCnt());
		shopInfo.setAvailableCnt(requestInfo.getAvailableCnt());
		shopInfo.setInfo(requestInfo.getInfo());

		ShopInfo updatedShopInfo = shopInfoRepository.saveAndFlush(shopInfo);

		return shopInfoMapper.toInfoResponse(updatedShopInfo);
	}

	@Transactional
	public ShopInfoDto.PriceResponse updatePrice(
		Long shopId,
		ShopInfoDto.Price requestPrice,
		Long memberId
	) throws BusinessException {
		log.debug("Updating price for member: {}", memberId);

		ShopInfo shopInfo = getShopInfoByShopId(shopId);

		shopInfo.setPrice(requestPrice.getPrice());

		shopInfo.clearPriceImages();  // 기존 이미지 모두 제거

		// 새 가격 이미지 업로드
		if (requestPrice.getPriceImage() != null) {
			PriceImage newPriceImage = PriceImage.builder().shopInfo(shopInfo).build();
			try {
				ImageDto imageDto = priceImageService.uploadImage(requestPrice.getPriceImage(), newPriceImage).get();
				newPriceImage.setBucketName(imageDto.getBucketName());
				newPriceImage.setObjectName(imageDto.getObjectName());
				shopInfo.addPriceImage(newPriceImage);
			} catch (InterruptedException | ExecutionException e) {
				throw new BusinessException(ImageErrorCode.UPLOAD_FAILURE);
			}
		}

		ShopInfo updatedShopInfo = shopInfoRepository.saveAndFlush(shopInfo);

		return shopInfoMapper.toPriceResponse(updatedShopInfo);
	}
}
