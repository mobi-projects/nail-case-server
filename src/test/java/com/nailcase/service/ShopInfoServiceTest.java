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

import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.repository.ShopInfoRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.StringGenerateFixture;
import com.nailcase.testUtils.fixture.ShopInfoFixture;

@ExtendWith(MockitoExtension.class)
class ShopInfoServiceTest {
	private final ShopInfoFixture shopInfoFixture = FixtureFactory.shopInfoFixture;
	@Mock
	private ShopRepository shopRepository;
	@Mock
	private ShopInfoRepository shopInfoRepository;
	@Mock
	private ShopService shopService;
	@InjectMocks
	private ShopInfoService shopInfoService;

	@Test
	@DisplayName("updateAddress 성공 테스트")
	void updateAddressSuccess() {
		// Given
		ShopInfo shopInfo = shopInfoFixture.getShopInfo();
		Shop shop = shopInfo.getShop();
		Long shopId = shop.getShopId();
		Long shopInfoId = shopInfo.getShopInfoId();
		Long memberId = shop.getMember().getMemberId();

		String updatedAddress = StringGenerateFixture.makeByNumbersAndAlphabets(10);
		String updatedPoint = StringGenerateFixture.makePoint();

		ShopInfoDto.Address request = new ShopInfoDto.Address();
		request.setAddress(updatedAddress);
		request.setPoint(updatedPoint);

		ShopInfoDto.Address response = new ShopInfoDto.Address();
		response.setAddress(updatedAddress);
		response.setPoint(updatedPoint);

		when(shopService.getShopById(shopId)).thenReturn(shop);
		when(shopInfoRepository.findByShopId(shopInfoId)).thenReturn(Optional.of(shopInfo));

		shop.setAddress(updatedAddress);
		shopInfo.setPoint(updatedPoint);

		when(shopRepository.saveAndFlush(any(Shop.class))).thenReturn(shop);
		when(shopInfoRepository.saveAndFlush(any(ShopInfo.class))).thenReturn(shopInfo);

		// When
		ShopInfoDto.Address result = shopInfoService.updateAddress(shopId, request, memberId);

		// Then
		assertNotNull(result);
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(response);

		verify(shopService, times(1)).getShopById(shopId);
		verify(shopInfoRepository, times(1)).findByShopId(shopId);
		verify(shopRepository, times(1)).saveAndFlush(shop);
		verify(shopInfoRepository, times(1)).saveAndFlush(shopInfo);
	}
}