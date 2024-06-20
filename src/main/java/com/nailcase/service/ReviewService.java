package com.nailcase.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.ReviewCommentDto;
import com.nailcase.model.dto.ReviewDto;
import com.nailcase.model.dto.ReviewImageDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Review;
import com.nailcase.model.entity.ReviewComment;
import com.nailcase.model.entity.ReviewImage;
import com.nailcase.model.entity.Shop;
import com.nailcase.repository.ReviewCommentsRepository;
import com.nailcase.repository.ReviewImageRepository;
import com.nailcase.repository.ReviewRepository;
import com.nailcase.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewCommentsRepository reviewCommentRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final ShopRepository shopRepository;
	private final ReviewImageService reviewImageService;

	@Transactional
	public List<ReviewImageDto> uploadImages(List<MultipartFile> files, Long memberId) {
		if (files.size() > 4) {
			throw new BusinessException(ImageErrorCode.IMAGE_LIMIT_EXCEEDED, "리뷰당 최대 3개의 이미지만 업로드할 수 있습니다.");
		}
		List<ReviewImage> tempImages = files.stream()
			.map(file -> {
				ReviewImage tempImage = new ReviewImage();
				tempImage.setCreatedBy(memberId);
				return tempImage;
			})
			.collect(Collectors.toList());

		List<ImageDto> savedImageDtos = reviewImageService.saveImages(files, tempImages);

		return savedImageDtos.stream()
			.map(savedImageDto -> ReviewImageDto.builder()
				.id(savedImageDto.getId())
				.bucketName(savedImageDto.getBucketName())
				.objectName(savedImageDto.getObjectName())
				.url(savedImageDto.getUrl())
				.createdBy(savedImageDto.getCreatedBy())
				.modifiedBy(savedImageDto.getModifiedBy())
				.build())
			.collect(Collectors.toList());
	}

	@Transactional
	public ReviewDto.Response registerReview(Long shopId, ReviewDto.Request request, Long memberId) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
		Member member = Member.builder()
			.memberId(memberId)
			.build();

		Review review = Review.builder()
			.shop(shop)
			.member(member)
			.contents(request.getContents())
			.rating(request.getRating())
			.build();

		if (request.getImageIds() != null && !request.getImageIds().isEmpty()) {
			List<ReviewImage> reviewImages = request.getImageIds().stream()
				.map(imageId -> {
					ReviewImage image = reviewImageRepository.findByImageId(imageId)
						.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));
					image.setReview(review); // 게시물과 이미지 연결
					return image;
				})
				.collect(Collectors.toList());

			reviewImageRepository.saveAll(reviewImages);
			review.registerReviewImages(reviewImages);
		}

		reviewRepository.save(review);
		return ReviewDto.Response.from(review);
	}

	@Transactional
	public ReviewDto.Response updateReview(Long shopId, Long reviewId, ReviewDto.Request request, Long memberId) {
		Review review = reviewRepository.findByShop_ShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
		if (!memberId.equals(review.getMember().getMemberId())) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		review.getReviewImages().forEach(reviewImage -> {
			reviewImageService.deleteImage(reviewImage.getObjectName());
			reviewImageRepository.delete(reviewImage);
		});
		review.getReviewImages().clear();

		// 새로운 이미지 연결
		List<ReviewImage> newPostImages = request.getImageIds().stream()
			.map(imageId -> reviewImageRepository.findByImageId(imageId)
				.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND)))
			.collect(Collectors.toList());
		review.getReviewImages().addAll(newPostImages);
		reviewImageRepository.saveAll(newPostImages);

		return ReviewDto.Response.from(review);
	}

	@Transactional
	public void addImageToReview(Long reviewId, List<MultipartFile> files) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

		List<ReviewImage> reviewImages = files.stream()
			.map(file -> {
				ReviewImage reviewImage = new ReviewImage();
				reviewImage.setReview(review);
				return reviewImage;
			})
			.toList();

		List<ImageDto> savedImages = reviewImageService.saveImages(files, reviewImages);

		reviewImages = savedImages.stream()
			.map(savedImage -> {
				ReviewImage reviewImage = new ReviewImage();
				reviewImage.setBucketName(savedImage.getBucketName());
				reviewImage.setObjectName(savedImage.getObjectName());
				reviewImage.setReview(review);
				return reviewImage;
			})
			.collect(Collectors.toList());

		review.getReviewImages().addAll(reviewImages);
		reviewImageRepository.saveAll(reviewImages);
		reviewRepository.save(review);
	}

	public void removeImageFromReview(Long reviewId, Long imageId, Long memberId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
		if (!review.getMember().getMemberId().equals(memberId)) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		ReviewImage reviewImage = reviewImageRepository.findByImageId(imageId)
			.orElseThrow(() -> new BusinessException(ImageErrorCode.IMAGE_NOT_FOUND));

		String objectName = reviewImage.getObjectName();
		reviewImageService.deleteImage(objectName);

		review.removeReviewImage(reviewImage);
		reviewImageRepository.delete(reviewImage);
	}

	public List<ReviewDto.Response> listReviews(Long shopId) {
		List<Review> reviews = reviewRepository.findByShop_ShopId(shopId);
		return reviews.stream()
			.map(ReviewDto.Response::from)
			.collect(Collectors.toList());
	}

	public ReviewDto.Response viewReview(Long shopId, Long reviewId) {
		Review review = reviewRepository.findByShop_ShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
		return ReviewDto.Response.from(review);
	}

	@Transactional
	public void deleteReview(Long shopId, Long reviewId, Long memberId) {
		Review review = reviewRepository.findByShop_ShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
		if (!memberId.equals(review.getMember().getMemberId())) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		reviewRepository.delete(review);
	}

	@Transactional
	public ReviewCommentDto.Response registerReviewComment(Long shopId, Long reviewId, ReviewCommentDto.Request request,
		Long memberId) {
		Review review = reviewRepository.findByShop_ShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));

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
		ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
		if (!reviewComment.getCreatedBy().equals(memberId)) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		reviewComment.updateContents(request.getContents());
		return ReviewCommentDto.Response.from(reviewComment);
	}

	@Transactional
	public void deleteReviewComment(Long shopId, Long reviewId, Long commentId, Long memberId) {
		ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
		if (!reviewComment.getCreatedBy().equals(memberId)) {
			throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		}
		reviewCommentRepository.delete(reviewComment);
	}

}