package com.nailcase.service;

import static com.nailcase.exception.codes.CommonErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.model.dto.ReviewCommentDto;
import com.nailcase.model.dto.ReviewDto;
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
	public Long registerReview(Long shopId, ReviewDto.Request request, List<MultipartFile> files) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		Review review = Review.builder()
			.shop(shop)
			.member(null) // 회원 정보 설정 필요
			.contents(request.getContents())
			.rating(request.getRating())
			.build();

		Review savedReview = reviewRepository.save(review);

		if (files != null && !files.isEmpty()) {
			List<ReviewImage> reviewImages = files.stream()
				.map(file -> {
					ReviewImage reviewImage = new ReviewImage();
					reviewImage.updateReview(savedReview);
					return reviewImage;
				})
				.collect(Collectors.toList());

			List<ImageDto> savedImages = reviewImageService.saveImages(files, reviewImages);

			reviewImages = savedImages.stream()
				.map(savedImage -> {
					ReviewImage reviewImage = new ReviewImage();
					reviewImage.setBucketName(savedImage.getBucketName());
					reviewImage.setObjectName(savedImage.getObjectName());
					reviewImage.updateReview(savedReview);
					return reviewImage;
				})
				.collect(Collectors.toList());

			savedReview.getReviewImages().addAll(reviewImages);
			reviewImageRepository.saveAll(reviewImages);
		}

		return savedReview.getReviewId();
	}

	public List<ReviewDto.Response> listReviews(Long shopId) {
		List<Review> reviews = reviewRepository.findByShopId(shopId);
		return reviews.stream()
			.map(ReviewDto.Response::from)
			.collect(Collectors.toList());
	}

	public ReviewDto.Response viewReview(Long shopId, Long reviewId) {
		Review review = reviewRepository.findByShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));
		return ReviewDto.Response.from(review);
	}

	@Transactional
	public void updateReview(Long shopId, Long reviewId, ReviewDto.Request request, List<MultipartFile> files) {
		Review review = reviewRepository.findByShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		review.getReviewImages().forEach(reviewImage -> {
			reviewImageService.deleteImage(reviewImage.getObjectName());
			reviewImageRepository.delete(reviewImage);
		});
		review.getReviewImages().clear();

		if (files != null && !files.isEmpty()) {
			List<ReviewImage> reviewImages = files.stream()
				.map(file -> {
					ReviewImage reviewImage = new ReviewImage();
					review.updateContents(request.getContents());
					review.updateRating(request.getRating());
					return reviewImage;
				})
				.collect(Collectors.toList());

			List<ImageDto> savedImages = reviewImageService.saveImages(files, reviewImages);

			reviewImages = savedImages.stream()
				.map(savedImage -> {
					ReviewImage reviewImage = new ReviewImage();
					reviewImage.setBucketName(savedImage.getBucketName());
					reviewImage.setObjectName(savedImage.getObjectName());
					reviewImage.updateReview(review);
					return reviewImage;
				})
				.collect(Collectors.toList());

			review.getReviewImages().addAll(reviewImages);
			reviewImageRepository.saveAll(reviewImages);
		}

		review.updateContents(request.getContents());
		review.updateRating(request.getRating());
	}

	@Transactional
	public void deleteReview(Long shopId, Long reviewId) {
		Review review = reviewRepository.findByShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		reviewRepository.delete(review);
	}

	@Transactional
	public Long registerReviewComment(Long shopId, Long reviewId, ReviewCommentDto.Request request) {
		Review review = reviewRepository.findByShopIdAndReviewId(shopId, reviewId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		ReviewComment reviewComment = ReviewComment.builder()
			.review(review)
			.contents(request.getContents())
			.build();

		ReviewComment savedReviewComment = reviewCommentRepository.save(reviewComment);
		return savedReviewComment.getReviewCommentId();
	}

	@Transactional
	public void updateReviewComment(Long shopId, Long reviewId, Long commentId, ReviewCommentDto.Request request) {
		ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		reviewComment.updateContents(request.getContents());

	}

	@Transactional
	public void deleteReviewComment(Long shopId, Long reviewId, Long commentId) {
		ReviewComment reviewComment = reviewCommentRepository.findById(commentId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		reviewCommentRepository.delete(reviewComment);
	}
}