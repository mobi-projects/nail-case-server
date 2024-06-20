package com.nailcase.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ShopErrorCode;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.Tag;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.repository.TagRepository;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.Reflection;
import com.nailcase.testUtils.StringGenerateFixture;
import com.nailcase.testUtils.fixtureFactory.ShopFixture;

@ExtendWith(MockitoExtension.class)
public class ShopServiceTest {
	@Mock
	private MemberRepository memberRepository;

	@Mock
	private ShopRepository shopRepository;

	@Mock
	private TagRepository tagRepository;

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
		ShopDto.Post request = shopToPostRequest(shop);
		ShopDto.Response response = shopMapper.toResponse(shop);

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(shopRepository.save(any(Shop.class))).thenReturn(shop);

		// When
		ShopDto.Response result = shopService.registerShop(request, memberId);

		// Then
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(response);

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
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(response);

		verify(shopRepository, times(1)).findById(shopId);
	}

	@Test
	@DisplayName("getShopById 실패 테스트 - ShopNotFound")
	void getShopByIdFailShopNotFound() {
		// Given
		Long shopId = 1L;
		when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

		// When
		BusinessException exception = assertThrows(BusinessException.class, () -> shopService.getShop(shopId));

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

	@Test
	@DisplayName("deleteShop 실패 테스트 - ShopDeletionForbidden")
	void deleteShopFailShopDeletionForbidden() {
		// Given
		Shop shop = shopFixture.getShop();
		Long invalidMemberId = -1L; // Invalid member ID

		when(shopRepository.findById(shop.getShopId())).thenReturn(Optional.of(shop));

		// When
		BusinessException exception = assertThrows(
			BusinessException.class,
			() -> shopService.deleteShop(shop.getShopId(), invalidMemberId));

		// Then
		assertEquals(ShopErrorCode.SHOP_DELETION_FORBIDDEN, exception.getErrorCode());

		verify(shopRepository, times(1)).findById(shop.getShopId());
		verify(shopRepository, times(0)).delete(any(Shop.class));
	}

	@Test
	@DisplayName("searchShop 성공 테스트")
	void searchShopSuccess() throws BusinessException {
		// Given
		String keyword = "test";
		Pageable pageable = PageRequest.of(0, 2);
		Shop shop1 = shopFixture.getShop();
		Shop shop2 = shopFixture.getShop(2L);
		List<Shop> shops = List.of(shop1, shop2);
		Page<Shop> shopPage = new PageImpl<>(shops, pageable, shops.size());

		when(shopRepository.searchShop(keyword, pageable)).thenReturn(shopPage);

		// When
		Page<ShopDto.Response> result = shopService.searchShop(keyword, pageable);

		// Then
		assertThat(result.getContent())
			.hasSize(shops.size())
			.containsExactlyInAnyOrderElementsOf(shops.stream()
				.map(shopMapper::toResponse)
				.collect(Collectors.toList()));
		assertTrue(result.getContent().contains(shopMapper.toResponse(shop1)));
	}

	@Test
	@DisplayName("updateShop 성공 테스트")
	void updateShopSuccess() throws Exception {
		// Given
		Shop existingShop = shopFixture.getShop();
		Long shopId = existingShop.getShopId();
		ShopDto.Post request = shopToPostRequest(existingShop);

		String updatedShopName = StringGenerateFixture.makeByNumbersAndAlphabets(10);
		String updatedPhone = StringGenerateFixture.makeByNumbersAndAlphabets(10);

		request.setShopName(updatedShopName);
		request.setPhone(updatedPhone);
		request.setAvailableSeats(10);

		Shop updatedShop = shopFixture.getShop();
		updatedShop.update(request);

		ShopDto.Response response = shopMapper.toResponse(updatedShop);

		when(shopRepository.findById(shopId)).thenReturn(Optional.of(existingShop));
		when(shopRepository.saveAndFlush(any(Shop.class))).thenReturn(updatedShop);

		// When
		ShopDto.Response result = shopService.updateShop(shopId, request);

		// Then
		assertNotNull(result);

		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(response);

		verify(shopRepository, times(1)).findById(shopId);
		verify(shopRepository, times(1)).saveAndFlush(existingShop);
	}

	@Test
	@DisplayName("getTags 성공 테스트")
	void getTagsSuccess() {
		// Given
		Tag tag1 = shopFixture.getTag();
		Tag tag2 = shopFixture.getTag(2L);
		List<Tag> tags = Arrays.asList(tag1, tag2);
		List<String> expectedTagNames = Arrays.asList(tag1.getTagName(), tag2.getTagName());

		when(tagRepository.findAll()).thenReturn(tags);

		// When
		List<String> result = shopService.getTags();

		// Then
		assertEquals(expectedTagNames.size(), result.size());
		assertThat(result).containsExactlyInAnyOrderElementsOf(expectedTagNames);

		verify(tagRepository, times(1)).findAll();
	}

	private ShopDto.Post shopToPostRequest(Shop shop) throws Exception {
		ShopDto.Post request = (ShopDto.Post)Reflection.createInstance(ShopDto.Post.class);
		Reflection.setField(request, "shopName", shop.getShopName());
		Reflection.setField(request, "availableSeats", shop.getAvailableSeats());

		return request;
	}
}
