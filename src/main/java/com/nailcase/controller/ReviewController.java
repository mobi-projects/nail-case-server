package com.nailcase.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.entity.review.comment.dto.ReviewCommentDto;
import com.nailcase.model.entity.review.dto.ReviewDto;
import com.nailcase.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long registerReview(@PathVariable Long shopId, @RequestBody ReviewDto.Request request) {
		return reviewService.registerReview(shopId, request);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<ReviewDto.Response> listReviews(@PathVariable Long shopId) {
		return reviewService.listReviews(shopId);
	}

	@GetMapping("/{reviewId}")
	@ResponseStatus(HttpStatus.OK)
	public ReviewDto.Response viewReview(@PathVariable Long shopId, @PathVariable Long reviewId) {
		return reviewService.viewReview(shopId, reviewId);
	}

	@PostMapping("/{reviewId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public Long registerReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId,
		@RequestBody ReviewCommentDto.Request request) {
		return reviewService.registerReviewComment(shopId, reviewId, request);
	}

	@PutMapping("/{reviewId}/comments/{commentId}")
	@ResponseStatus(HttpStatus.OK)
	public void updateReviewComment(
		@PathVariable Long shopId,
		@PathVariable Long reviewId,
		@PathVariable Long commentId,
		@RequestBody ReviewCommentDto.Request request
	) {
		reviewService.updateReviewComment(shopId, reviewId, commentId, request);
	}

	@DeleteMapping("/{reviewId}/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteReviewComment(
		@PathVariable Long shopId,
		@PathVariable Long reviewId,
		@PathVariable Long commentId
	) {
		reviewService.deleteReviewComment(shopId, reviewId, commentId);
	}
}