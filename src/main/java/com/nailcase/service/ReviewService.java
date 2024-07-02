package com.nailcase.service;

import java.util.List;
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
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.ReviewCommentDto;
import com.nailcase.model.dto.ReviewDto;
import com.nailcase.model.dto.ReviewImageDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.QReview;
import com.nailcase.model.entity.QReviewComment;
import com.nailcase.model.entity.QReviewImage;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.Review;
import com.nailcase.model.entity.ReviewComment;
import com.nailcase.model.entity.ReviewImage;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.ReviewCommentsRepository;
import com.nailcase.repository.ReviewImageRepository;
import com.nailcase.repository.ReviewRepository;
import com.nailcase.util.ServiceUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

	private final Executor imageExecutor;
	private final EntityManager entityManager;
	private final JPAQueryFactory queryFactory;
	private final TransactionTemplate transactionTemplate;
	private final ReviewRepository reviewRepository;
	private final ReviewCommentsRepository reviewCommentRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final ReviewImageService reviewImageService;

	@Transactional
	public CompletableFuture<List<ReviewImageDto>> uploadImages(List<MultipartFile> files, Long memberId) {
		if (files.size() > 3) {
			throw new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED, "리뷰당 최대 3개의 이미지만 업로드할 수 있습니다.");
		}
		List<ReviewImage> tempImages = files.stream()
			.map(file -> {
				ReviewImage tempImage = new ReviewImage();
				tempImage.setCreatedBy(memberId);
				return tempImage;
			})
			.collect(Collectors.toList());

		return reviewImageService.saveImagesAsync(files, tempImages)
			.thenApply(savedImageDtos -> savedImageDtos.stream()
				.map(savedImageDto -> ReviewImageDto.builder()
					.id(savedImageDto.getId())
					.bucketName(savedImageDto.getBucketName())
					.objectName(savedImageDto.getObjectName())
					.url(savedImageDto.getUrl())
					.createdBy(savedImageDto.getCreatedBy())
					.build())
				.collect(Collectors.toList()));
	}

	@Transactional
	public ReviewDto.Response registerReview(Long shopId, ReviewDto.Request reviewRequest, Long memberId) {
		Shop shop = queryFactory.selectFrom(QShop.shop)
			.where(QShop.shop.shopId.eq(shopId)).fetchOne();

		ServiceUtils.checkNullValue(shop);

		Member member = Member.builder()
			.memberId(memberId)
			.build();

		Review review = Review.builder()
			.shop(shop)
			.member(member)
			.contents(reviewRequest.getContents())
			.rating(reviewRequest.getRating())
			.build();
		Review savedReview = reviewRepository.save(review);

		if (reviewRequest.getImageIds() != null && !reviewRequest.getImageIds().isEmpty()) {
			List<ReviewImage> reviewImages = reviewImageRepository.findAllById(reviewRequest.getImageIds());

			if (reviewImages.size() != reviewRequest.getImageIds().size()) {
				throw new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND);
			}

			reviewImages.forEach(image -> {
				image.setReview(savedReview);
				savedReview.addReviewImage(image);
			});
			reviewImageRepository.saveAll(reviewImages);
		}

		return ReviewDto.Response.from(review);
	}

	@Transactional
	@Async("imageExecutor")
	public CompletableFuture<ReviewDto.Response> updateReview(Long shopId, Long reviewId,
		ReviewDto.Request reviewRequest, Long memberId) {
		return CompletableFuture.supplyAsync(() -> transactionTemplate.execute(status -> {
			Review review = queryFactory.selectFrom(QReview.review)
				.leftJoin(QReview.review.shop).fetchJoin()
				.where(QReview.review.shop.shopId.eq(shopId)
					.and(QReview.review.reviewId.eq(reviewId)))
				.fetchOne();

			ServiceUtils.checkNullValue(review);

			if (!memberId.equals(review.getMember().getMemberId())) {
				throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
			}

			review.updateContents(reviewRequest.getContents());
			review.updateRating(reviewRequest.getRating());

			List<Long> oldImageIds = queryFactory
				.select(QReviewImage.reviewImage.imageId)
				.from(QReviewImage.reviewImage)
				.where(QReviewImage.reviewImage.review.eq(review))
				.fetch();

			List<Long> imagesToDelete = oldImageIds.stream()
				.filter(id -> !reviewRequest.getImageIds().contains(id))
				.toList();

			CompletableFuture<Void> deleteFuture = CompletableFuture.runAsync(() -> {
				transactionTemplate.execute(txStatus -> {
					if (!imagesToDelete.isEmpty()) {
						queryFactory.update(QReviewImage.reviewImage)
							.setNull(QReviewImage.reviewImage.review)
							.where(QReviewImage.reviewImage.imageId.in(imagesToDelete))
							.execute();
						imagesToDelete.forEach(imageId ->
							reviewImageService.deleteImageAsync(
								queryFactory
									.select(QReviewImage.reviewImage.objectName)
									.from(QReviewImage.reviewImage)
									.where(QReviewImage.reviewImage.imageId.eq(imageId))
									.fetchOne()
							)
						);
					}
					return null;
				});
			});

			CompletableFuture<Void> addFuture = CompletableFuture.runAsync(() -> {
				transactionTemplate.execute(txStatus -> {
					List<Long> newImageIds = reviewRequest.getImageIds().stream()
						.filter(id -> !oldImageIds.contains(id))
						.collect(Collectors.toList());

					if (!newImageIds.isEmpty()) {
						queryFactory.update(QReviewImage.reviewImage)
							.set(QReviewImage.reviewImage.review, review)
							.where(QReviewImage.reviewImage.imageId.in(newImageIds))
							.execute();
					}
					return null;
				});
			});

			CompletableFuture.allOf(deleteFuture, addFuture).join();

			entityManager.flush();
			entityManager.clear();

			Review refreshedReview = queryFactory
				.selectFrom(QReview.review)
				.leftJoin(QReview.review.reviewImages, QReviewImage.reviewImage).fetchJoin()
				.where(QReview.review.reviewId.eq(reviewId))
				.fetchOne();

			if (refreshedReview == null) {
				throw new BusinessException(CommonErrorCode.NOT_FOUND);
			}

			return ReviewDto.Response.from(refreshedReview);
		}), imageExecutor);

	}

	@Transactional
	public void addImageToReview(Long shopId, Long reviewId, List<MultipartFile> files) {
		Review review = queryFactory.selectFrom(QReview.review)
			.where(QReview.review.shop.shopId.eq(shopId).and(QReview.review.reviewId.eq(reviewId))).fetchOne();

		ServiceUtils.checkNullValue(review);

		List<ReviewImage> reviewImages = files.stream()
			.map(file -> {
				ReviewImage reviewImage = new ReviewImage();
				reviewImage.setReview(review);
				return reviewImage;
			})
			.toList();

		reviewImageService.saveImagesAsync(files, reviewImages)
			.thenCompose(savedImages -> {
				List<ReviewImage> newReviewImages = savedImages.stream()
					.map(savedImage -> {
						ReviewImage reviewImage = new ReviewImage();
						reviewImage.setBucketName(savedImage.getBucketName());
						reviewImage.setObjectName(savedImage.getObjectName());
						reviewImage.setReview(review);
						return reviewImage;
					})
					.toList();

				// QueryDSL을 사용하여 새 이미지와 Post를 연결
				CompletableFuture<Void> updateImagesFuture = CompletableFuture.runAsync(() -> {
					queryFactory.update(QReviewImage.reviewImage)
						.set(QReviewImage.reviewImage.review, review)
						.where(QReviewImage.reviewImage.imageId.in(newReviewImages.stream()
							.map(ReviewImage::getImageId)
							.collect(Collectors.toList())))
						.execute();
				}, imageExecutor);

				return updateImagesFuture.thenRun(() -> {
					review.getReviewImages().addAll(newReviewImages);
					reviewImageRepository.saveAll(newReviewImages);
					reviewRepository.save(review);
				});
			});
	}

	@Transactional
	public void removeImageFromReview(Long shopId, Long reviewId, Long imageId, Long memberId) {

		Review review = queryFactory.selectFrom(QReview.review)
			.where(QReview.review.shop.shopId.eq(shopId).and(QReview.review.reviewId.eq(reviewId))).fetchOne();

		ServiceUtils.checkNullValue(review);

		if (!review.getMember().getMemberId().equals(memberId)) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}

		ReviewImage reviewImage = queryFactory.selectFrom(QReviewImage.reviewImage)
			.where(QReviewImage.reviewImage.imageId.eq(imageId)).fetchOne();

		ServiceUtils.checkNullValue(reviewImage);

		String objectName = reviewImage.getObjectName();

		// 비동기 이미지 삭제
		reviewImageService.deleteImageAsync(objectName)
			.thenRun(() -> transactionTemplate.execute(status -> {
				queryFactory.update(QReviewImage.reviewImage)
					.setNull(QReviewImage.reviewImage.review)
					.where(QReviewImage.reviewImage.imageId.eq(imageId))
					.execute();

				queryFactory.delete(QReviewImage.reviewImage)
					.where(QReviewImage.reviewImage.imageId.eq(imageId))
					.execute();

				return null;
			}));
	}

	public List<ReviewDto.Response> listReviews(Long shopId) {
		QReview review = QReview.review;
		List<Review> reviews = queryFactory
			.select(review)
			.from(review)
			.where(review.shop.shopId.eq(shopId))
			.fetch();
		return reviews.stream()
			.map(ReviewDto.Response::from)
			.collect(Collectors.toList());
	}

	public ReviewDto.Response viewReview(Long shopId, Long reviewId) {
		Review review = queryFactory.select(QReview.review)
			.where(QReview.review.shop.shopId.eq(shopId).and(QReview.review.reviewId.eq(reviewId)))
			.fetchOne();

		ServiceUtils.checkNullValue(review);

		return ReviewDto.Response.from(review);
	}

	@Transactional
	public void deleteReview(Long shopId, Long reviewId, Long memberId) {
		Review review = queryFactory.selectFrom(QReview.review)
			.where(QReview.review.shop.shopId.eq(shopId).and(QReview.review.reviewId.eq(reviewId)))
			.fetchOne();

		ServiceUtils.checkNullValue(review);

		if (!memberId.equals(review.getMember().getMemberId())) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		List<CompletableFuture<Void>> deleteImageFutures = review.getReviewImages().stream()
			.map(reviewImage -> reviewImageService.deleteImageAsync(reviewImage.getObjectName()))
			.toList();

		CompletableFuture.allOf(deleteImageFutures.toArray(new CompletableFuture[0]))
			.thenRun(() -> {
				review.getReviewImages().clear();
				reviewRepository.delete(review);
			});
	}

	@Transactional
	public ReviewCommentDto.Response registerReviewComment(Long shopId, Long reviewId, ReviewCommentDto.Request request,
		Long memberId) {
		Review review = queryFactory.selectFrom(QReview.review)
			.where(QReview.review.reviewId.eq(reviewId).and(QReview.review.shop.shopId.eq(shopId))).fetchOne();

		ServiceUtils.checkNullValue(review);

		ReviewComment reviewComment = ReviewComment.builder()
			.review(review)
			.contents(request.getContents())
			.build();

		reviewCommentRepository.save(reviewComment);
		return ReviewCommentDto.Response.from(reviewComment);
	}

	@Transactional
	public ReviewCommentDto.Response updateReviewComment(Long shopId, Long reviewId, Long commentId,
		ReviewCommentDto.Request request,
		Long memberId) {
		ReviewComment reviewComment = queryFactory
			.selectFrom(QReviewComment.reviewComment)
			.join(QReviewComment.reviewComment.review, QReview.review).fetchJoin()
			.join(QReview.review.shop, QShop.shop).fetchJoin()
			.where(QReviewComment.reviewComment.reviewCommentId.eq(commentId)
				.and(QReview.review.shop.shopId.eq(shopId))
				.and(QReview.review.reviewId.eq(reviewId)))
			.fetchOne();

		ServiceUtils.checkNullValue(reviewComment);

		if (!reviewComment.getCreatedBy().equals(memberId)) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		reviewComment.updateContents(request.getContents());
		return ReviewCommentDto.Response.from(reviewComment);
	}

	@Transactional
	public void deleteReviewComment(Long shopId, Long reviewId, Long commentId, Long memberId) {
		ReviewComment reviewComment = queryFactory
			.selectFrom(QReviewComment.reviewComment)
			.where(QReviewComment.reviewComment.reviewCommentId.eq(commentId))
			.fetchOne();

		ServiceUtils.checkNullValue(reviewComment);

		if (!reviewComment.getCreatedBy().equals(memberId)) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		reviewCommentRepository.delete(reviewComment);
	}

}