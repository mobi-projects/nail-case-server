package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
	@DisplayName("샵 동록시 ROLE MANAGER | shop 저장, shopInfo, shopHour 초기화 된다.")
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
		assertEquals(response.getShopId(), result.getShopId());
		assertEquals(response.getOwnerId(), result.getOwnerId());
		assertEquals(response.getShopName(), result.getShopName());
		assertEquals(response.getPhone(), result.getPhone());
		assertEquals(response.getOverview(), result.getOverview());
		assertEquals(response.getAddress(), result.getAddress());
		assertEquals(response.getAvailableSeats(), result.getAvailableSeats());
		assertEquals(response.getCreatedAt(), result.getCreatedAt());
		assertEquals(response.getModifiedAt(), result.getModifiedAt());

		verify(memberRepository).findById(memberId);
		assertEquals(Role.MANAGER, member.getRole());

		verify(shopRepository, times(1)).save(any(Shop.class));
		verify(shopInfoService, times(1)).initShopInfo(any(Shop.class));
		verify(shopHourService, times(1)).initShopHour(any(Shop.class));
	}
}
