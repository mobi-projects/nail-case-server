package com.nailcase.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nailcase.mapper.ShopInfoMapper;
import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.repository.ShopInfoRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.Reflection;
import com.nailcase.testUtils.StringGenerateFixture;
import com.nailcase.testUtils.fixture.ShopInfoFixture;

@ExtendWith(MockitoExtension.class)
class ShopInfoServiceTest {
	private final ShopInfoFixture shopInfoFixture = FixtureFactory.shopInfoFixture;
	private final ShopInfoMapper shopInfoMapper = ShopInfoMapper.INSTANCE;
	@Mock
	private ShopRepository shopRepository;
	@Mock
	private ShopInfoRepository shopInfoRepository;
	@Mock
	private ShopService shopService;
	@InjectMocks
	private ShopInfoService shopInfoService;

	@Test
	@DisplayName("getShopInfo 성공 테스트")
	void getShopInfoSuccess() {
		// Given
		ShopInfo shopInfo = shopInfoFixture.getShopInfo();
		Long shopId = shopInfo.getShopId();
		ShopInfoDto.Response response = shopInfoMapper.toResponse(shopInfo);

		when(shopInfoRepository.findByShopId(shopId)).thenReturn(Optional.of(shopInfo));

		// When
		ShopInfoDto.Response result = shopInfoService.getShopInfo(shopId);

		// Then
		assertNotNull(result);
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(response);

		verify(shopInfoRepository, times(1)).findByShopId(shopId);
	}

	@Test
	@DisplayName("updateAddress 성공 테스트")
	void updateAddressSuccess() {
		// Given
		ShopInfo shopInfo = shopInfoFixture.getShopInfo();
		Shop shop = shopInfo.getShop();
		Long shopId = shop.getShopId();
		Long shopInfoId = shopInfo.getShopInfoId();
		Long nailArtistId = shop.getNailArtist().getNailArtistId();

		String updatedAddress = StringGenerateFixture.makeByNumbersAndAlphabets(10);
		String updatedPoint = StringGenerateFixture.makePoint();

		ShopInfoDto.Address address = ShopInfoDto.Address.builder()
			.address(updatedAddress)
			.point(updatedPoint).build();

		when(shopService.getShopById(shopId)).thenReturn(shop);
		when(shopInfoRepository.findByShopId(shopInfoId)).thenReturn(Optional.of(shopInfo));

		shop.setAddress(updatedAddress);
		shopInfo.setPoint(updatedPoint);

		when(shopRepository.saveAndFlush(any(Shop.class))).thenReturn(shop);
		when(shopInfoRepository.saveAndFlush(any(ShopInfo.class))).thenReturn(shopInfo);

		// When
		ShopInfoDto.Address result = shopInfoService.updateAddress(shopId, address, nailArtistId);

		// Then
		assertNotNull(result);
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(address);

		verify(shopService, times(1)).getShopById(shopId);
		verify(shopInfoRepository, times(1)).findByShopId(shopId);
		verify(shopRepository, times(1)).saveAndFlush(shop);
		verify(shopInfoRepository, times(1)).saveAndFlush(shopInfo);
	}

	@Test
	@DisplayName("updateInfo 성공 테스트")
	void updateInfoSuccess() {
		// Given
		ShopInfo shopInfo = shopInfoFixture.getShopInfo();
		Long shopId = shopInfo.getShopId();
		Long nailArtistId = shopInfo.getShop().getNailArtist().getNailArtistId();

		int updatedParkingLotCnt = 10;
		int updatedAvailableCnt = 10;
		String updatedInfo = StringGenerateFixture.makeByNumbersAndAlphabets(10);

		ShopInfoDto.Info info = ShopInfoDto.Info.builder()
			.parkingLotCnt(updatedParkingLotCnt)
			.availableCnt(updatedAvailableCnt)
			.info(updatedInfo).build();

		when(shopInfoRepository.findByShopId(shopId)).thenReturn(Optional.of(shopInfo));

		shopInfo.setParkingLotCnt(updatedParkingLotCnt);
		shopInfo.setAvailableCnt(updatedAvailableCnt);
		shopInfo.setInfo(updatedInfo);

		when(shopInfoRepository.saveAndFlush(any(ShopInfo.class))).thenReturn(shopInfo);

		// When
		ShopInfoDto.Info result = shopInfoService.updateInfo(shopId, info, nailArtistId);

		// Then
		assertNotNull(result);
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(info);

		verify(shopInfoRepository, times(1)).findByShopId(shopId);
		verify(shopInfoRepository, times(1)).saveAndFlush(shopInfo);
	}

	@Test
	@DisplayName("updatePrice 성공 테스트")
	void updatePriceSuccess() throws Exception {
		// Given
		ShopInfo shopInfo = shopInfoFixture.getShopInfo();
		Long shopId = shopInfo.getShopId();
		Long nailArtistId = shopInfo.getShop().getNailArtist().getNailArtistId();

		String updatedPrice = StringGenerateFixture.makeByNumbersAndAlphabets(10);

		ShopInfoDto.Price request = (ShopInfoDto.Price)Reflection.createInstance(ShopInfoDto.Price.class);
		request.setPrice(updatedPrice);
		// TODO img

		when(shopInfoRepository.findByShopId(shopId)).thenReturn(Optional.of(shopInfo));
		shopInfo.setPrice(updatedPrice);
		when(shopInfoRepository.saveAndFlush(any(ShopInfo.class))).thenReturn(shopInfo);

		ShopInfoDto.PriceResponse response = shopInfoMapper.toPriceResponse(shopInfo);

		// When
		ShopInfoDto.PriceResponse result = shopInfoService.updatePrice(shopId, request, nailArtistId);

		// Then
		assertNotNull(result);
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(response);

		verify(shopInfoRepository, times(1)).findByShopId(shopId);
		verify(shopInfoRepository, times(1)).saveAndFlush(shopInfo);
	}
}
