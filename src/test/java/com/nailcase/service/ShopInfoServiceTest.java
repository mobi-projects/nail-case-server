package com.nailcase.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nailcase.mapper.ShopInfoMapper;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.repository.ShopInfoRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.StringGenerateFixture;
import com.nailcase.testUtils.fixture.ShopFixture;
import com.nailcase.testUtils.fixture.ShopInfoFixture;

@ExtendWith(MockitoExtension.class)
class ShopInfoServiceTest {
	private final ShopInfoFixture shopInfoFixture = FixtureFactory.shopInfoFixture;
	private final ShopFixture shopFixture = FixtureFactory.shopFixture;

	@Mock
	private ShopMapper mockShopInfoMapper;

	@Mock
	private ShopRepository shopRepository;
	@Mock
	private ShopInfoRepository shopInfoRepository;
	@Mock
	private ShopService shopService;
	@Mock
	private PriceImageService priceImageService;
	@Mock
	private ShopInfoMapper shopInfoMapper;
	@InjectMocks
	private ShopInfoService shopInfoService;

	@Test
	@DisplayName("updateAddress 성공 테스트")
	void updateAddressSuccess() {
		// Given
		ShopInfo shopInfo = shopInfoFixture.getShopInfo();

		NailArtist nailArtist = NailArtist.builder()
			.nailArtistId(1L) // 적절한 ID로 설정
			.build();

		Shop shop = Shop.builder()
			.shopId(shopInfo.getShopInfoId()) // 단방향 관계에서 Shop의 ID를 사용하여 ShopInfo를 매핑
			.nailArtist(nailArtist) // NailArtist를 설정
			.build();

		Long shopId = shop.getShopId();
		Long nailArtistId = nailArtist.getNailArtistId(); // 여기서 가져옵니다

		String updatedAddress = StringGenerateFixture.makeByNumbersAndAlphabets(10);
		String updatedPoint = StringGenerateFixture.makePoint();

		ShopInfoDto.Address address = ShopInfoDto.Address.builder()
			.address(updatedAddress)
			.point(updatedPoint).build();

		when(shopService.getShopById(shopId)).thenReturn(shop);
		when(shopService.getShopInfoByShopId(shopId)).thenReturn(shopInfo);

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
		verify(shopService, times(1)).getShopInfoByShopId(shopId);
		verify(shopRepository, times(1)).saveAndFlush(shop);
		verify(shopInfoRepository, times(1)).saveAndFlush(shopInfo);
	}

	@Test
	@DisplayName("updateInfo 성공 테스트")
	void updateInfoSuccess() {
		// Given
		ShopInfo shopInfo = shopInfoFixture.getShopInfo();

		NailArtist nailArtist = NailArtist.builder()
			.nailArtistId(1L) // 적절한 ID로 설정
			.build();

		Shop shop = Shop.builder()
			.shopId(shopInfo.getShopInfoId()) // 단방향 관계에서 Shop의 ID를 사용하여 ShopInfo를 매핑
			.nailArtist(nailArtist) // NailArtist를 설정
			.build();

		Long shopId = shop.getShopId();
		Long nailArtistId = nailArtist.getNailArtistId(); // 여기서 가져옵니다

		int updatedParkingLotCnt = 10;
		int updatedAvailableCnt = 10;
		String updatedInfo = StringGenerateFixture.makeByNumbersAndAlphabets(10);

		ShopInfoDto.Info info = ShopInfoDto.Info.builder()
			.parkingLotCnt(updatedParkingLotCnt)
			.availableCnt(updatedAvailableCnt)
			.info(updatedInfo).build();

		when(shopService.getShopInfoByShopId(shopId)).thenReturn(shopInfo);

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

		verify(shopService, times(1)).getShopInfoByShopId(shopId);
		verify(shopInfoRepository, times(1)).saveAndFlush(shopInfo);
	}

}
