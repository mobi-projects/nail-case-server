package com.nailcase.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.model.dto.ReviewCommentDto;
import com.nailcase.model.dto.ReviewDto;
import com.nailcase.model.dto.ReviewImageDto;
import com.nailcase.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shops/{shopId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ReviewDto.Response registerReview(@PathVariable Long shopId, @RequestBody ReviewDto.Request request,
		@AuthenticationPrincipal
		Long userId) {

		log.info("Uploading images for shopId: {}", shopId);
		return reviewService.registerReview(shopId, request, userId);
	}

	@PutMapping("/{reviewId}")
	public ReviewDto.Response updateReview(@PathVariable Long shopId, @PathVariable Long reviewId,
		@RequestBody ReviewDto.Request request, @AuthenticationPrincipal
	Long userId
	) {
		log.info("Updating review: {} for shopId: {}", reviewId, shopId);

		return reviewService.updateReview(shopId, reviewId, request, userId);
	}

	@DeleteMapping("/{reviewId}")
	public void deleteReview(@PathVariable Long shopId, @PathVariable Long reviewId,
		@AuthenticationPrincipal Long userId) {
		reviewService.deleteReview(shopId, reviewId, userId);
	}

	@PostMapping("/{reviewId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public ReviewCommentDto.Response registerReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId,
		@RequestBody ReviewCommentDto.Request request, @AuthenticationPrincipal Long userId) {
		return reviewService.registerReviewComment(shopId, reviewId, request, userId);
	}

	@PutMapping("/{reviewId}/comments/{commentId}")
	public ReviewCommentDto.Response updateReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId,
		@PathVariable Long commentId, @RequestBody ReviewCommentDto.Request request,
		@AuthenticationPrincipal Long userId) {
		return reviewService.updateReviewComment(shopId, reviewId, commentId, request, userId);
	}

	@DeleteMapping("/{reviewId}/comments/{commentId}")
	public void deleteReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId,
		@PathVariable Long commentId, @AuthenticationPrincipal Long userId) {
		reviewService.deleteReviewComment(shopId, reviewId, commentId, userId);
	}

	// 이미지만 업로드하는 API
	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public List<ReviewImageDto> uploadImages(@RequestParam("files") List<MultipartFile> files,
		@AuthenticationPrincipal Long userId, @PathVariable Long shopId) {
		log.info("Uploading images for shopId: {}", shopId);
		return reviewService.uploadImages(files, userId);
	}

	@PostMapping("/{reviewId}/images")
	@ResponseStatus(HttpStatus.CREATED)
	public void addImageToReview(@PathVariable Long reviewId, @RequestParam("files") List<MultipartFile> files,
		@PathVariable String shopId) {
		log.info("Adding images to post: {} for shopId: {}", reviewId, shopId);
		reviewService.addImageToReview(reviewId, files);
	}

	@DeleteMapping("/{reviewId}/images/{imageId}")
	public void removeImageFromReview(@PathVariable Long reviewId, @PathVariable Long imageId,
		@PathVariable String shopId, @AuthenticationPrincipal Long userId) {
		log.info("Removing image: {} from post: {}", imageId, reviewId);
		reviewService.removeImageFromReview(reviewId, imageId, userId);
	}

	@GetMapping
	public List<ReviewDto.Response> listReviews(@PathVariable Long shopId) {
		return reviewService.listReviews(shopId);
	}

	@GetMapping("/{reviewId}")
	public ReviewDto.Response viewReview(@PathVariable Long shopId, @PathVariable Long reviewId) {
		return reviewService.viewReview(shopId, reviewId);
	}

}