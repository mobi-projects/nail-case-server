package com.nailcase.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.ShopErrorCode;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.Tag;
import com.nailcase.model.entity.TagMapping;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.repository.TagMappingRepository;
import com.nailcase.repository.TagRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
	private final ShopMapper shopMapper = ShopMapper.INSTANCE;
	private final ShopRepository shopRepository;
	private final MemberRepository memberRepository;
	private final TagRepository tagRepository;
	private final TagMappingRepository tagMappingRepository;
	private final ShopInfoService shopInfoService;
	private final ShopHourService shopHourService;

	@Transactional
	public ShopDto.Response registerShop(
		ShopDto.Post request,
		Long memberId
	) throws BusinessException {
		// Set member role MANAGER
		Member member = memberRepository
			.findById(memberId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

		member.setRole(Role.MANAGER);

		// Create shop
		Shop shop = Shop.builder()
			.shopName(request.getShopName())
			.phone(request.getPhone())
			.member(member)
			.build();

		Shop savedShop = shopRepository.save(shop);

		// Create shop info, shop hour init
		shopInfoService.initShopInfo(savedShop);
		shopHourService.initShopHour(savedShop);

		return shopMapper.toResponse(savedShop);
	}

	@Transactional(readOnly = true)
	public ShopDto.Response getShop(Long shopId) throws BusinessException {
		// TODO 이미지
		// TODO 여기서 방문자 수 처리?
		// TODO info hours 추가해주기
		return shopMapper.toResponse(getShopById(shopId));
	}

	@Transactional
	public void deleteShop(Long shopId, Long memberId) throws BusinessException {
		Shop shop = getShopById(shopId);

		if (!shop.getMember().getMemberId().equals(memberId)) {
			throw new BusinessException(ShopErrorCode.SHOP_DELETION_FORBIDDEN);
		}

		shopRepository.delete(shop);

		// TODO 사진 관련 처리
	}

	@Transactional(readOnly = true)
	public Page<ShopDto.Response> searchShop(String keyword, Pageable pageable) throws BusinessException {
		return shopRepository.searchShop(keyword, pageable).map(shopMapper::toResponse);
	}

	@Transactional
	public ShopDto.Response updateShop(Long shopId, ShopDto.Post putRequest) throws BusinessException {
		Shop shop = getShopById(shopId);

		// TODO 샵에 속해있는 아티스트 인지 권한 검사

		shop.update(putRequest);
		Shop updatedShop = shopRepository.saveAndFlush(shop);

		return shopMapper.toResponse(updatedShop);
	}

	@Transactional(readOnly = true)
	public List<String> getTags() {
		return tagRepository.findAll().stream().map(Tag::getTagName).toList();
	}

	private Shop getShopById(Long shopId) throws BusinessException {
		return shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_NOT_FOUND));
	}

	@Transactional
	public ShopDto.Response updateOverview(Long shopId, ShopDto.Patch patchRequest, Long memberId)
		throws BusinessException {
		// TODO 샵에 속해있는 아티스트 인지 검사
		log.debug(String.valueOf(memberId)); // TODO remove

		// Update overview
		Shop shop = getShopById(shopId);
		shop.setOverview(patchRequest.getOverview());

		// Tag
		List<String> tagNames = patchRequest.getTagNames();

		// Save only new tags in the tag table
		List<Tag> tags = tagNames.stream()
			.map(tagName -> tagRepository.findByTagName(tagName)
				.orElseGet(() -> {
					Tag tag = Tag.builder().tagName(tagName).build();
					return tagRepository.save(tag);
				}))
			.toList();

		// Save the tag mappings with sort order
		List<TagMapping> tagMappings = IntStream.range(0, tagNames.size())
			.mapToObj(i -> {
				Tag tag = tags.get(i);
				return TagMapping.builder().shop(shop).tag(tag).sortOrder(i).build();
			})
			.collect(Collectors.toList());

		tagMappingRepository.saveAll(tagMappings);
		Shop updatedShop = shopRepository.saveAndFlush(shop);

		return shopMapper.toResponse(updatedShop);
	}
}
