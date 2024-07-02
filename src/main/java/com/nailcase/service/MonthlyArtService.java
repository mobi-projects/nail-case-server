package com.nailcase.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.model.dto.MonthlyArtDto;
import com.nailcase.model.dto.MonthlyArtImageDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.MonthlyArt;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.model.entity.MonthlyArtLikedMember;
import com.nailcase.model.entity.QMember;
import com.nailcase.model.entity.QMonthlyArt;
import com.nailcase.model.entity.QMonthlyArtImage;
import com.nailcase.model.entity.QMonthlyArtLikedMember;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.MonthlyArtImageRepository;
import com.nailcase.repository.MonthlyArtLikedMemberRepository;
import com.nailcase.repository.MonthlyArtRepository;
import com.nailcase.repository.ShopRepository;
import com.nailcase.util.ServiceUtils;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MonthlyArtService {
	private final Executor imageExecutor;
	private final EntityManager entityManager;
	private final JPAQueryFactory queryFactory;
	private final TransactionTemplate transactionTemplate;
	private final MonthlyArtRepository monthlyArtRepository;
	private final MonthlyArtImageRepository monthlyArtImageRepository;
	private final MonthlyArtImageService monthlyArtImageService;
	private final MemberRepository memberRepository;
	private final ShopRepository shopRepository;
	private final MonthlyArtLikedMemberRepository monthlyArtLikedMemberRepository;
	private final BitmapService bitmapService;
	private final ReviewImageService reviewImageService;

	@Transactional
	public CompletableFuture<List<MonthlyArtImageDto>> uploadImages(List<MultipartFile> files, Long memberId) {
		if (files.size() > 6) {
			throw new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED, "이달의 아트 게시물당 최대 5개의 이미지만 업로드할 수 있습니다.");
		}

		List<MonthlyArtImage> tempImages = files.stream()
			.map(file -> {
				MonthlyArtImage tempImage = new MonthlyArtImage();
				tempImage.setCreatedBy(memberId);
				return tempImage;
			})
			.collect(Collectors.toList());

		return monthlyArtImageService.saveImagesAsync(files, tempImages)
			.thenApply(savedImageDtos -> savedImageDtos.stream()
				.map(savedImageDto -> MonthlyArtImageDto.builder()
					.id(savedImageDto.getId())
					.bucketName(savedImageDto.getBucketName())
					.objectName(savedImageDto.getObjectName())
					.url(savedImageDto.getUrl())
					.createdBy(savedImageDto.getCreatedBy())
					.build())
				.collect(Collectors.toList()));
	}

	@Transactional
	public MonthlyArtDto.Response registerMonthlyArt(Long shopId, MonthlyArtDto.Request monthlyArtRequest) {
		Shop shop = queryFactory.selectFrom(QShop.shop)
			.where(QShop.shop.shopId.eq(shopId)).fetchOne();

		ServiceUtils.checkNullValue(shop);

		MonthlyArt monthlyArt = MonthlyArt.builder()
			.title(monthlyArtRequest.getTitle())
			.shop(shop)
			.build();

		MonthlyArt savedMonthlyArt = monthlyArtRepository.save(monthlyArt);

		if (monthlyArtRequest.getImageIds() != null && !monthlyArtRequest.getImageIds().isEmpty()) {
			List<MonthlyArtImage> monthlyArtImages = monthlyArtImageRepository.findAllById(
				monthlyArtRequest.getImageIds());

			if (monthlyArtImages.size() != monthlyArtRequest.getImageIds().size()) {
				throw new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND);
			}

			monthlyArtImages.forEach(image -> {
				image.setMonthlyArt(savedMonthlyArt);
				savedMonthlyArt.addImage(image);
			});
			monthlyArtImageRepository.saveAll(monthlyArtImages);
		}

		return MonthlyArtDto.Response.from(savedMonthlyArt, false);
	}

	@Transactional
	@Async("imageExecutor")
	public CompletableFuture<MonthlyArtDto.Response> updateMonthlyArt(Long shopId, Long monthlyArtId,
		MonthlyArtDto.Request monthlyArtRequest, Long memberId) {
		return CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
			MonthlyArt monthlyArt = queryFactory.selectFrom(QMonthlyArt.monthlyArt)
				.leftJoin(QMonthlyArt.monthlyArt.shop).fetchJoin()
				.where(QMonthlyArt.monthlyArt.shop.shopId.eq(shopId)
					.and(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId)))
				.fetchOne();

			ServiceUtils.checkNullValue(monthlyArt);

			boolean alreadyLiked = monthlyArtLikedMemberRepository.existsByMonthlyArt_MonthlyArtIdAndMember_MemberId(
				monthlyArtId, memberId);

			monthlyArt.updateTitle(monthlyArtRequest.getTitle());
			monthlyArt.updateContents(monthlyArtRequest.getContents());

			List<Long> oldImageIds = queryFactory
				.select(QMonthlyArtImage.monthlyArtImage.imageId)
				.from(QMonthlyArtImage.monthlyArtImage)
				.where(QMonthlyArtImage.monthlyArtImage.monthlyArt.eq(monthlyArt))
				.fetch();

			List<Long> imagesToDelete = oldImageIds.stream()
				.filter(id -> !monthlyArtRequest.getImageIds().contains(id))
				.collect(Collectors.toList());

			CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
				transactionTemplate.execute(txStatus -> {
					if (!imagesToDelete.isEmpty()) {
						queryFactory.update(QMonthlyArtImage.monthlyArtImage)
							.setNull(QMonthlyArtImage.monthlyArtImage.monthlyArt)
							.where(QMonthlyArtImage.monthlyArtImage.imageId.in(imagesToDelete))
							.execute();
						imagesToDelete.forEach(imageId ->
							monthlyArtImageService.deleteImageAsync(
								queryFactory
									.select(QMonthlyArtImage.monthlyArtImage.objectName)
									.from(QMonthlyArtImage.monthlyArtImage)
									.where(QMonthlyArtImage.monthlyArtImage.imageId.eq(imageId))
									.fetchOne()
							)
						);
					}
					return null;
				});
			});

			CompletableFuture<Void> addFuture = CompletableFuture.runAsync(() -> {
				transactionTemplate.execute(txStatus -> {
					List<Long> newImageIds = monthlyArtRequest.getImageIds().stream()
						.filter(id -> !oldImageIds.contains(id))
						.collect(Collectors.toList());

					if (!newImageIds.isEmpty()) {
						queryFactory.update(QMonthlyArtImage.monthlyArtImage)
							.set(QMonthlyArtImage.monthlyArtImage.monthlyArt, monthlyArt)
							.where(QMonthlyArtImage.monthlyArtImage.imageId.in(newImageIds))
							.execute();
					}
					return null;
				});
			});

			CompletableFuture.allOf(deleteFuture, addFuture).join();

			entityManager.flush();
			entityManager.clear();

			MonthlyArt refreshedMonthlyArt = queryFactory
				.selectFrom(QMonthlyArt.monthlyArt)
				.leftJoin(QMonthlyArt.monthlyArt.monthlyArtImages, QMonthlyArtImage.monthlyArtImage).fetchJoin()
				.where(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId))
				.fetchOne();

			ServiceUtils.checkNullValue(refreshedMonthlyArt);

			return MonthlyArtDto.Response.from(refreshedMonthlyArt, alreadyLiked);
		}), imageExecutor);
	}

	@Transactional
	public void addImageToMonthlyArt(Long shopId, Long monthlyArtId, List<MultipartFile> files) {
		MonthlyArt monthlyArt = queryFactory.selectFrom(QMonthlyArt.monthlyArt)
			.leftJoin(QMonthlyArt.monthlyArt.shop).fetchJoin()
			.where(QMonthlyArt.monthlyArt.shop.shopId.eq(shopId)
				.and(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId)))
			.fetchOne();

		ServiceUtils.checkNullValue(monthlyArt);

		List<MonthlyArtImage> monthlyArtImages = files.stream()
			.map(file -> {
				MonthlyArtImage monthlyArtImage = new MonthlyArtImage();
				monthlyArtImage.setMonthlyArt(monthlyArt);
				return monthlyArtImage;
			})
			.collect(Collectors.toList());

		monthlyArtImageService.saveImagesAsync(files, monthlyArtImages)
			.thenCompose(savedImages -> {
				List<MonthlyArtImage> newMonthlyArtImages = savedImages.stream()
					.map(savedImage -> {
						MonthlyArtImage monthlyArtImage = new MonthlyArtImage();
						monthlyArtImage.setBucketName(savedImage.getBucketName());
						monthlyArtImage.setObjectName(savedImage.getObjectName());
						monthlyArtImage.setMonthlyArt(monthlyArt);
						return monthlyArtImage;
					})
					.toList();

				// QueryDSL을 사용하여 새 이미지와 Post를 연결
				CompletableFuture<Void> updateImagesFuture = CompletableFuture.runAsync(() -> {
					queryFactory.update(QMonthlyArtImage.monthlyArtImage)
						.set(QMonthlyArtImage.monthlyArtImage.monthlyArt, monthlyArt)
						.where(QMonthlyArtImage.monthlyArtImage.imageId.in(newMonthlyArtImages.stream()
							.map(MonthlyArtImage::getImageId)
							.collect(Collectors.toList())))
						.execute();
				}, imageExecutor);

				return updateImagesFuture.thenRun(() -> {
					monthlyArt.getMonthlyArtImages().addAll(newMonthlyArtImages);
					monthlyArtImageRepository.saveAll(newMonthlyArtImages);
					monthlyArtRepository.save(monthlyArt);
				});
			});
	}

	public CompletableFuture<Void> removeImageFromMonthlyArt(Long shopId, Long monthlyArtId, Long imageId) {
		MonthlyArt monthlyArt = queryFactory.selectFrom(QMonthlyArt.monthlyArt)
			.leftJoin(QMonthlyArt.monthlyArt.monthlyArtImages, QMonthlyArtImage.monthlyArtImage).fetchJoin()
			.where(
				QMonthlyArt.monthlyArt.shop.shopId.eq(shopId).and(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId)))
			.fetchOne();

		ServiceUtils.checkNullValue(monthlyArt);

		MonthlyArtImage monthlyArtImage = monthlyArt.getMonthlyArtImages().stream().findFirst().orElse(null);
		ServiceUtils.checkNullValue(monthlyArtImage);

		String objectName = monthlyArtImage.getObjectName();

		return monthlyArtImageService.deleteImageAsync(objectName)
			.thenRun(() -> transactionTemplate.execute(status -> {
				queryFactory.update(QMonthlyArtImage.monthlyArtImage)
					.setNull(QMonthlyArtImage.monthlyArtImage.monthlyArt)
					.where(QMonthlyArtImage.monthlyArtImage.imageId.eq(imageId))
					.execute();

				queryFactory.delete(QMonthlyArtImage.monthlyArtImage)
					.where(QMonthlyArtImage.monthlyArtImage.imageId.eq(imageId))
					.execute();

				return null;
			}));
	}

	public List<MonthlyArtDto.Response> listMonthlyArts(Long shopId, Long memberId) {
		QMonthlyArt monthlyArt = QMonthlyArt.monthlyArt;
		QMonthlyArtLikedMember monthlyArtLikedMember = QMonthlyArtLikedMember.monthlyArtLikedMember;

		// Shop ID를 기준으로 Posts와 PostLikedMembers를 함께 조회
		List<Tuple> results = queryFactory
			.select(monthlyArt, monthlyArtLikedMember.monthlyArt.monthlyArtId)
			.from(monthlyArt)
			.leftJoin(monthlyArtLikedMember)
			.on(monthlyArtLikedMember.monthlyArt.monthlyArtId.eq(monthlyArt.monthlyArtId)
				.and(monthlyArtLikedMember.member.memberId.eq(memberId)))
			.where(monthlyArt.shop.shopId.eq(shopId))
			.fetch();

		// Post와 Liked 정보를 매핑
		return results.stream()
			.filter(tuple -> tuple != null && tuple.get(monthlyArt) != null)
			.collect(Collectors.groupingBy(tuple -> Optional.ofNullable(tuple.get(monthlyArt))))
			.entrySet().stream()
			.map(entry -> {
				MonthlyArt monthlyEntity = entry.getKey()
					.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
				boolean liked = entry.getValue().stream()
					.anyMatch(tuple -> tuple.get(monthlyArtLikedMember.monthlyArt.monthlyArtId) != null);
				return MonthlyArtDto.Response.from(monthlyEntity, liked);
			})
			.collect(Collectors.toList());
	}

	public MonthlyArtDto.Response viewMonthlyArt(Long shopId, Long monthlyArtId, Long memberId) {
		MonthlyArt monthlyArt = queryFactory.selectFrom(QMonthlyArt.monthlyArt)
			.join(QMonthlyArt.monthlyArt.shop, QShop.shop).fetchJoin()
			.where(
				QMonthlyArt.monthlyArt.shop.shopId.eq(shopId).and(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId)))
			.fetchOne();

		ServiceUtils.checkNullValue(monthlyArt);

		boolean alreadyLiked = monthlyArtLikedMemberRepository.existsByMonthlyArt_MonthlyArtIdAndMember_MemberId(
			monthlyArtId, memberId);

		// 조회수 증가 로직
		String key = "monthlyArt:view:count:" + monthlyArtId;
		Long offset = memberId;
		Boolean alreadyViewed = bitmapService.getBit(key, offset).orElse(false);

		if (!alreadyViewed) {
			bitmapService.setBit(key, offset, true);
			Long viewCount = bitmapService.bitCount(key).orElse(0L);
			monthlyArt.incrementViews(viewCount);
			monthlyArtRepository.save(monthlyArt);
		}

		Long currentViewCount = bitmapService.bitCount(key).orElse(0L);
		return MonthlyArtDto.Response.from(monthlyArt, alreadyLiked);
	}

	@Transactional
	public void deleteMonthlyArt(Long shopId, Long monthlyArtId) {
		MonthlyArt monthlyArt = queryFactory.select(QMonthlyArt.monthlyArt)
			.from(QMonthlyArt.monthlyArt)
			.join(QMonthlyArt.monthlyArt.shop, QShop.shop).fetchJoin()
			.where(QShop.shop.shopId.eq(shopId).and(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId)))
			.fetchOne();

		ServiceUtils.checkNullValue(monthlyArt);

		List<CompletableFuture<Void>> deleteImageFutures = monthlyArt.getMonthlyArtImages().stream()
			.map(monthlyArtImage -> monthlyArtImageService.deleteImageAsync(monthlyArtImage.getObjectName()))
			.toList();

		CompletableFuture.allOf(deleteImageFutures.toArray(new CompletableFuture[0]))
			.thenRun(() -> {
				monthlyArt.getMonthlyArtImages().clear();
				monthlyArtRepository.delete(monthlyArt);
			});
	}

	@Transactional
	public void likeMonthlyArt(Long monthlyArtId, Long memberId) {
		MonthlyArt monthlyArt = queryFactory.selectFrom(QMonthlyArt.monthlyArt)
			.where(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId))
			.fetchOne();

		ServiceUtils.checkNullValue(monthlyArt);

		Member currentMember = queryFactory.selectFrom(QMember.member)
			.where(QMember.member.memberId.eq(memberId))
			.fetchOne();

		ServiceUtils.checkNullValue(currentMember);

		boolean alreadyLiked = !queryFactory.selectFrom(QMonthlyArtLikedMember.monthlyArtLikedMember)
			.where(QMonthlyArtLikedMember.monthlyArtLikedMember.monthlyArt.monthlyArtId.eq(monthlyArtId)
				.and(QMonthlyArtLikedMember.monthlyArtLikedMember.member.memberId.eq(memberId)))
			.fetch().isEmpty();

		if (!alreadyLiked) {
			MonthlyArtLikedMember monthlyArtLikedMember = new MonthlyArtLikedMember();
			monthlyArtLikedMember.updateMonthlyArt(monthlyArt);
			monthlyArtLikedMember.updateMember(currentMember);
			monthlyArtLikedMemberRepository.save(monthlyArtLikedMember);
			monthlyArt.incrementLikes();
			monthlyArtRepository.save(monthlyArt);
		}
	}

	@Transactional
	public void unlikeMonthlyArt(Long monthlyArtId, Long memberId) {
		MonthlyArtLikedMember monthlyArtLikedMember = queryFactory.selectFrom(
				QMonthlyArtLikedMember.monthlyArtLikedMember)
			.where(QMonthlyArtLikedMember.monthlyArtLikedMember.monthlyArt.monthlyArtId.eq(monthlyArtId)
				.and(QMonthlyArtLikedMember.monthlyArtLikedMember.member.memberId.eq(memberId)))
			.fetchOne();

		ServiceUtils.checkNullValue(monthlyArtLikedMember);

		monthlyArtLikedMemberRepository.delete(monthlyArtLikedMember);

		MonthlyArt monthlyArt = queryFactory.selectFrom(QMonthlyArt.monthlyArt)
			.where(QMonthlyArt.monthlyArt.monthlyArtId.eq(monthlyArtId))
			.fetchOne();

		ServiceUtils.checkNullValue(monthlyArt);

		monthlyArt.decrementLikes();
		monthlyArtRepository.save(monthlyArt);
	}
}