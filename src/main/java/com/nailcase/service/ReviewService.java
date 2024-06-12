package com.nailcase.service;

import static com.nailcase.exception.codes.CommonErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.model.entity.review.Review;
import com.nailcase.model.entity.review.Shop;
import com.nailcase.model.entity.review.ShopRepository;
import com.nailcase.model.entity.review.comment.ReviewComment;
import com.nailcase.model.entity.review.comment.dto.ReviewCommentDto;
import com.nailcase.model.entity.review.dto.ReviewDto;
import com.nailcase.repository.ReviewCommentsRepository;
import com.nailcase.repository.ReviewsRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewsRepository reviewRepository;
	private final ReviewCommentsRepository reviewCommentRepository;
	private final ShopRepository shopRepository;

	@Transactional
	public Long registerReview(Long shopId, ReviewDto.Request request) {
		Shop shop = shopRepository.findById(shopId)
			.orElseThrow(() -> new BusinessException(NOT_FOUND));

		Review review = Review.builder()
			.shop(shop)
			.member(null) // 회원 정보 설정 필요
			.shopMember(null) // 샵 회원 정보 설정 필요
			.title(request.getTitle())
			.rating(request.getRating())
			.build();

		Review savedReview = reviewRepository.save(review);
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