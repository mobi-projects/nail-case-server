package com.nailcase.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
	@PostMapping
	public void registerReview(@PathVariable Long shopId) {
	}

	@GetMapping
	public void listReviews(@PathVariable Long shopId) {
	}

	@GetMapping("/{reviewId}")
	public void viewReview(@PathVariable Long shopId, @PathVariable Long reviewId) {
	}

	@PostMapping("/{reviewId}/comments")
	public void registerReviewComment(@PathVariable Long shopId, @PathVariable Long reviewId) {
	}

	@PutMapping("/{reviewId}/comments/{commentId}")
	public void updateReviewComment(
		@PathVariable Long shopId,
		@PathVariable Long reviewId,
		@PathVariable Long commentId
	) {
	}

	@DeleteMapping("/{reviewId}/comments/{commentId}")
	public void deleteReviewComment(
		@PathVariable String shopId,
		@PathVariable Long reviewId,
		@PathVariable Long commentId
	) {
	}
}
