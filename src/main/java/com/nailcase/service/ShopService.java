package com.nailcase.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.ConcurrencyErrorCode;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.exception.codes.ShopErrorCode;
import com.nailcase.exception.codes.ShopInfoErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.mapper.ShopMapper;
import com.nailcase.model.dto.NailArtistDto;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.dto.WorkHourDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.PriceImage;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.model.entity.ShopInfo;
import com.nailcase.model.entity.ShopLikedMember;
import com.nailcase.model.entity.Tag;
import com.nailcase.model.entity.TagMapping;
import com.nailcase.model.entity.WorkHour;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;
import com.nailcase.repository.ShopImageRepository;
import com.nailcase.repository.ShopLikedMemberRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.repository.TagMappingRepository;
import com.nailcase.repository.TagRepository;
import com.nailcase.repository.WorkHourRepository;
import com.nailcase.util.DateUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
	private final EntityManager entityManager;
	private final ShopMapper shopMapper = ShopMapper.INSTANCE;
	private final ShopRepository shopRepository;
	private final NailArtistRepository nailArtistRepository;
	private final TagRepository tagRepository;
	private final ShopImageRepository shopImageRepository;
	private final TagMappingRepository tagMappingRepository;
	private final WorkHourRepository workHourRepository;
	private final ShopImageService shopImageService;
	private final PriceImageService priceImageService;
	private final MemberRepository memberRepository;
	private final ShopLikedMemberRepository shopLikedMemberRepository;
	private final ObjectMapper objectMapper;

	@Transactional
	public ShopDto.Response registerShop(String shopDataJson, List<MultipartFile> profileImages,
		List<MultipartFile> priceImages, UserPrincipal userPrincipal) {
		try {
			ShopDto.PostRequest shopData = parseShopData(shopDataJson);
			NailArtist nailArtist = getNailArtist(userPrincipal.getId());
			Shop shop = createAndSaveShop(shopData, nailArtist);

			processShopDetails(shop, shopData, profileImages, priceImages);

			Shop refreshedShop = refreshShop(shop.getShopId());
			return shopMapper.toResponse(refreshedShop);
		} catch (Exception e) {
			log.error("샵 등록 실패", e);
			throw new BusinessException(ShopErrorCode.REGISTRATION_FAILED, e);
		}
	}

	@Transactional
	public void deleteShop(Long shopId, Long nailArtistId) throws BusinessException {
		Shop shop = getShopById(shopId);

		if (!shop.getNailArtist().getNailArtistId().equals(nailArtistId)) {
			throw new BusinessException(ShopErrorCode.SHOP_DELETION_FORBIDDEN);
		}

		shopRepository.delete(shop);

		// TODO 사진 관련 처리
	}

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

	public List<String> getTags() {
		return tagRepository.findAll().stream().map(Tag::getTagName).toList();
	}

	public Shop getShopById(Long shopId) throws BusinessException {
		return shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_NOT_FOUND));
	}

	@Transactional
	public ShopDto.Response updateOverview(Long shopId, ShopDto.Patch patchRequest, Long nailArtistId)
		throws BusinessException {
		// TODO 샵에 속해있는 아티스트 인지 검사
		log.debug(String.valueOf(nailArtistId)); // TODO remove

		// Update overview
		Shop shop = getShopById(shopId);
		shop.setOverview(patchRequest.getOverview());

		// Tag
		Set<TagMapping> existingTagMappings = shop.getTags();
		List<String> newTagNames = patchRequest.getTagNames();
		Set<String> newTagNamesSet = new HashSet<>(newTagNames);

		// Tags to be removed
		List<TagMapping> tagMappingsToRemove = existingTagMappings.stream()
			.filter(t -> !newTagNamesSet.contains(t.getTag().getTagName()))
			.toList();

		// Remove old tag mappings
		tagMappingRepository.deleteAll(tagMappingsToRemove);

		// Save only new tags in the tag table
		List<Tag> tags = newTagNames.stream()
			.map(tagName -> tagRepository.findByTagName(tagName)
				.orElseGet(() -> {
					Tag tag = Tag.builder().tagName(tagName).build();
					return tagRepository.save(tag);
				}))
			.toList();

		// Save the tag mappings with sort order
		List<TagMapping> tagMappings = IntStream.range(0, newTagNames.size())
			.mapToObj(i -> {
				Tag tag = tags.get(i);
				return TagMapping.builder().shop(shop).tag(tag).sortOrder(i).build();
			})
			.collect(Collectors.toList());

		tagMappingRepository.saveAll(tagMappings);
		Shop updatedShop = shopRepository.saveAndFlush(shop);

		return shopMapper.toResponse(updatedShop);
	}

	@Transactional
	public CompletableFuture<String> uploadImage(Long shopId, MultipartFile file, Long memberId) throws
		BusinessException {
		log.debug(String.valueOf(memberId)); // TODO remove

		Shop shop = getShopById(shopId);

		if (shop.getShopImages().size() == 4) {
			throw new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED);
		}

		ShopImage shopImage = ShopImage.builder().shop(shop).build();
		return shopImageService.uploadImage(file, shopImage)
			.thenApply(ImageDto::getUrl);
	}

	@Transactional
	public void deleteImage(Long imageId, Long memberId) throws BusinessException {
		ShopImage shopImage = shopImageRepository.findById(imageId)
			.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));

		// TODO 샵에 속해있는 아티스트 인지 검사
		Shop shop = shopImage.getShop();
		log.debug(shop.toString()); // TODO remove
		log.debug(String.valueOf(memberId)); // TODO remove

		shopImageService.deleteImage(shopImage.getObjectName());
		shopImageRepository.delete(shopImage);
	}

	@Transactional
	protected void initWorkHour(Shop shop) {
		IntStream.range(0, 7)
			.mapToObj(i -> WorkHour.builder().shop(shop).dayOfWeek(i).build())
			.forEach(workHourRepository::save);
	}

	public List<Shop> findTopPopularShops(Pageable pageable) {
		return shopRepository.findTopShopsByPopularityCriteria(pageable).getContent();
	}

	public List<Shop> findMemberLikedShops(Long memberId, Pageable pageable) {
		return shopRepository.findLikedShopsByMember(memberId, pageable).getContent();
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public boolean toggleLike(Long shopId, Long memberId) {
		try {
			Shop shop = shopRepository.findById(shopId)
				.orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_NOT_FOUND));
			Member currentMember = memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

			ShopLikedMember existingLike = shopLikedMemberRepository.findByShop_ShopIdAndMember_MemberId(shopId,
					memberId)
				.orElse(null);

			boolean liked;

			if (existingLike == null) {
				log.info("좋아요가 없음: shopId={}, memberId={}", shopId, memberId);
				// 좋아요가 없는 경우 추가
				ShopLikedMember newLike = new ShopLikedMember();
				newLike.updateShop(shop);
				newLike.updateMember(currentMember);
				shopLikedMemberRepository.save(newLike);
				shop.incrementLikes();
				liked = true;
				log.info("좋아요 추가됨: shopId={}, memberId={}", shopId, memberId);
			} else {
				log.info("좋아요가 이미 있음: shopId={}, memberId={}", shopId, memberId);
				// 좋아요가 있는 경우 제거
				shopLikedMemberRepository.delete(existingLike);
				shop.decrementLikes();
				liked = false;
				log.info("좋아요 제거됨: shopId={}, memberId={}", shopId, memberId);
			}

			shopRepository.save(shop);
			log.info("최종 좋아요 상태: shopId={}, memberId={}, liked={}", shopId, memberId, liked);
			return liked;
		} catch (OptimisticLockException e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("낙관적 락킹 실패 ", e);
			throw new BusinessException(ConcurrencyErrorCode.OPTIMISTIC_LOCK_ERROR,
				"현재 다른 사용자가 같은 작업을 수행 중입니다. 잠시 후 다시 시도해주세요.");
		} catch (Exception e) {
			log.error("toggle like 실행중 예상치 못한 예외 발생 : ", e);
			throw new BusinessException(ConcurrencyErrorCode.UPDATE_FAILURE,
				"좋아요 상태 변경 중 예기치 않은 에러가 발생했습니다.");
		}
	}

	public List<NailArtistDto.ListResponse> listShopNailArtist(Long shopId) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_NOT_FOUND));
		List<NailArtist> shopNailArtists = nailArtistRepository.findByShop_ShopId(shopId);
		return shopNailArtists.stream()
			.map(NailArtistDto.ListResponse::fromEntity)
			.collect(Collectors.toList());
	}

	public Shop findByShopIdAndNailArtistsAndWorkHours(Long shopId) {
		return shopRepository.findByShopIdAndNailArtistsAndWorkHours(shopId)
			.orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_NOT_FOUND));
	}

	private void saveWorkHours(Shop shop, List<WorkHourDto.Post> workHourDtoPosts) {
		workHourDtoPosts.forEach(dto -> {
			WorkHour workHour = WorkHour.builder()
				.shop(shop)
				.dayOfWeek(dto.getDayOfWeek())
				.isOpen(dto.getIsOpen())
				.openTime(DateUtils.unixTimeStampToLocalDateTime(dto.getOpenTime()))
				.closeTime(DateUtils.unixTimeStampToLocalDateTime(dto.getCloseTime()))
				.build();
			shop.addWorkHour(workHour);
		});
	}

	public ShopInfo getShopInfoByShopId(Long shopId) throws BusinessException {
		return shopRepository.findById(shopId)
			.map(Shop::getShopInfo)
			.orElseThrow(() -> new BusinessException(ShopInfoErrorCode.SHOP_INFO_NOT_FOUND));
	}

	private ShopDto.PostRequest parseShopData(String shopDataJson) throws JsonProcessingException {
		return objectMapper.readValue(shopDataJson, ShopDto.PostRequest.class);
	}

	private NailArtist getNailArtist(Long id) {
		return nailArtistRepository.findById(id)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));
	}

	private Shop createAndSaveShop(ShopDto.PostRequest shopData, NailArtist nailArtist) {
		ShopDto.PostResponse postResponse = new ShopDto.PostResponse(shopData, null, null);
		Shop shop = shopMapper.postResponseToShop(postResponse);
		shop.setNailArtist(nailArtist);
		nailArtist.addShop(shop);
		return shopRepository.save(shop);
	}

	private void processShopDetails(Shop shop, ShopDto.PostRequest shopData,
		List<MultipartFile> profileImages, List<MultipartFile> priceImages) {
		saveWorkHours(shop, shopData.getWorkHours());
		saveImages(shop, profileImages, this::saveShopImages);
		shopRepository.flush();
		saveImages(shop, priceImages, this::savePriceImages);
	}

	private <T extends MultipartFile> void saveImages(Shop shop, List<T> images,
		BiConsumer<List<T>, Shop> saveFunction) {
		if (images != null && !images.isEmpty()) {
			saveFunction.accept(images, shop);
		}
	}

	private void saveShopImages(List<MultipartFile> files, Shop shop) {
		List<ShopImage> shopImages = createImageEntities(files, ShopImage::new, shop::addShopImage);
		shopImageService.saveImagesSync(files, shopImages);
	}

	private void savePriceImages(List<MultipartFile> files, Shop shop) {
		List<PriceImage> priceImages = createImageEntities(files, PriceImage::new, shop::addPriceImage);
		priceImageService.saveImagesSync(files, priceImages);
	}

	private <T> List<T> createImageEntities(List<MultipartFile> files, Supplier<T> entityCreator,
		Consumer<T> entityAdder) {
		return files.stream()
			.map(file -> {
				T entity = entityCreator.get();
				entityAdder.accept(entity);
				return entity;
			})
			.collect(Collectors.toList());
	}

	private Shop refreshShop(Long shopId) {
		return shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(ShopErrorCode.SHOP_NOT_FOUND));
	}

	public ShopDto.Response getShop(Long shopId) throws BusinessException {
		Shop shop = getShopById(shopId);
		double avgRating = shopRepository.calculateShopReviewRating(shopId);
		ShopDto.Response response = shopMapper.toResponse(shop);
		response.setShopAvgRatings(avgRating == 0.0 ? null : avgRating);
		return response;
	}

}
