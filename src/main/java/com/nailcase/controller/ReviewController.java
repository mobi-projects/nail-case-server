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

import com.nailcase.model.dto.MemberDetails;
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

	// 이미지만 업로드하는 API
	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public List<ReviewImageDto> uploadImages(@RequestParam("files") List<MultipartFile> files,
		@AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long shopId) {
		Long memberId = memberDetails.getMemberId();
		log.info("Uploading images for shopId: {}", shopId);
		return reviewService.uploadImages(files, memberId);
	}

	@PostMapping("/{reviewId}/images")
	@ResponseStatus(HttpStatus.CREATED)
	public void addImageToReview(@PathVariable Long reviewId, @RequestParam("files") List<MultipartFile> files,
		@PathVariable String shopId) {
		log.info("Adding images to post: {} for shopId: {}", reviewId, shopId);
		reviewService.addImageToReview(reviewId, files);
	}

	@DeleteMapping("/{reviewId}/images/{imageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeImageFromReview(@PathVariable Long reviewId, @PathVariable Long imageId,
		@PathVariable String shopId, @AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		log.info("Removing image: {} from post: {}", imageId, reviewId);
		reviewService.removeImageFromReview(reviewId, imageId, memberId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ReviewDto.Response registerReview(@PathVariable Long shopId, @RequestBody ReviewDto.Request request,
		@AuthenticationPrincipal
		MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		log.info("Uploading images for shopId: {} by {}", shopId, memberId);
		return reviewService.registerReview(shopId, request, memberId);
	}

	@PutMapping("/{reviewId}")
	public ReviewDto.Response updateReview(@PathVariable Long shopId, @PathVariable Long reviewId,
		@RequestBody ReviewDto.Request request, @AuthenticationPrincipal MemberDetails memberDetails
	) {
		log.info("Updating review: {} for shopId: {}", reviewId, shopId);
		Long memberId = memberDetails.getMemberId();
		return reviewService.updateReview(shopId, reviewId, request, memberId);
	}

	@GetMapping
	public List<ReviewDto.Response> listReviews(@PathVariable Long shopId) {
		return reviewService.listReviews(shopId);
	}

	@GetMapping("/{reviewId}")
	public ReviewDto.Response viewReview(@PathVariable Long shopId, @PathVariable Long reviewId) {
		return reviewService.viewReview(shopId, reviewId);
	}

	@DeleteMapping("/{reviewId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteReview(@PathVariable Long shopId, @PathVariable Long reviewId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		reviewService.deleteReview(shopId, reviewId, memberId);
	}

	@PostMapping("/{reviewId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public ReviewCommentDto.Response registerReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId,
		@RequestBody ReviewCommentDto.Request request, @AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		return reviewService.registerReviewComment(shopId, reviewId, request, memberId);
	}

	@PutMapping("/{reviewId}/comments/{commentId}")
	public ReviewCommentDto.Response updateReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId,
		@PathVariable Long commentId, @RequestBody ReviewCommentDto.Request request,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		return reviewService.updateReviewComment(shopId, reviewId, commentId, request, memberId);
	}

	@DeleteMapping("/{reviewId}/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId,
		@PathVariable Long commentId, @AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		reviewService.deleteReviewComment(shopId, reviewId, commentId, memberId);
	}
}