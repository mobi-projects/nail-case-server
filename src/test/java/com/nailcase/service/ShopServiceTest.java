package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ShopErrorCode;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.Reflection;
import com.nailcase.testUtils.fixtureFactory.ShopFixture;

@ExtendWith(MockitoExtension.class)
public class ShopServiceTest {
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private ShopRepository shopRepository;

	@Mock
	private ShopInfoService shopInfoService;

	@Mock
	private ShopHourService shopHourService;

	@InjectMocks
	private ShopService shopService;

	private final ShopFixture shopFixture = FixtureFactory.shopFixture;
	private final ShopMapper shopMapper = ShopMapper.INSTANCE;

	@Test
	@DisplayName("샵 동록시 ROLE MANAGER | shop 저장, shopInfo, shopHour 초기화 성공 테스트")
	void registerShopSuccess() throws Exception {
		// Given
		Shop shop = shopFixture.getShop();
		Member member = shop.getMember();
		Long memberId = member.getMemberId();
		ShopDto.Post request = (ShopDto.Post)Reflection.createInstance(ShopDto.Post.class);
		ShopDto.Response response = shopMapper.toResponse(shop);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(shopRepository.save(any(Shop.class))).thenReturn(shop);

		// When
		ShopDto.Response result = shopService.registerShop(request, memberId);

		// Then
		assertTrue(equals(response, result));

		verify(memberRepository).findById(memberId);
		assertEquals(Role.MANAGER, member.getRole());

		verify(shopRepository, times(1)).save(any(Shop.class));
		verify(shopInfoService, times(1)).initShopInfo(any(Shop.class));
		verify(shopHourService, times(1)).initShopHour(any(Shop.class));
	}

	@Test
	@DisplayName("getShopById 성공 테스트")
	void getShopByIdSuccess() throws BusinessException {
		// Given
		Shop shop = shopFixture.getShop();
		Long shopId = shop.getShopId();
		ShopDto.Response response = shopMapper.toResponse(shop);

		when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

		// When
		ShopDto.Response result = shopService.getShop(shopId);

		// Then
		assertTrue(equals(response, result));
		verify(shopRepository, times(1)).findById(shopId);
	}

	@Test
	@DisplayName("getShopById 실패 테스트 - ShopNotFound")
	void getShopByIdFailShopNotFound() {
		// Given
		Long shopId = 1L;
		when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

		// When
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			shopService.getShop(shopId);
		});

		// Then
		assertEquals(ShopErrorCode.SHOP_NOT_FOUND, exception.getErrorCode());
		verify(shopRepository, times(1)).findById(shopId);
	}

	@Test
	@DisplayName("deleteShop 성공 테스트")
	void deleteShopSuccess() throws BusinessException {
		// Given
		Shop shop = shopFixture.getShop();
		Long shopId = shop.getShopId();
		Long memberId = shop.getMember().getMemberId();

		when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

		// When
		shopService.deleteShop(shopId, memberId);

		// Then
		verify(shopRepository, times(1)).findById(shopId);
		verify(shopRepository, times(1)).delete(shop);

		ArgumentCaptor<Shop> shopCaptor = ArgumentCaptor.forClass(Shop.class);
		verify(shopRepository).delete(shopCaptor.capture());

		assertEquals(shop, shopCaptor.getValue());
	}

	private boolean equals(ShopDto.Response expected, ShopDto.Response actual) {
		return expected.getShopId().equals(actual.getShopId()) &&
			expected.getOwnerId().equals(actual.getOwnerId()) &&
			expected.getShopName().equals(actual.getShopName()) &&
			expected.getPhone().equals(actual.getPhone()) &&
			expected.getOverview().equals(actual.getOverview()) &&
			expected.getAddress().equals(actual.getAddress()) &&
			expected.getAvailableSeats().equals(actual.getAvailableSeats()) &&
			expected.getCreatedAt().equals(actual.getCreatedAt()) &&
			expected.getModifiedAt().equals(actual.getModifiedAt());
	}
}
