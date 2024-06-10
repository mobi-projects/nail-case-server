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
@RequestMapping("/shops/{shopId}/posts")
@RequiredArgsConstructor
public class PostController {
	@PostMapping("/")
	public void registerPost(@PathVariable Long shopId) {
	}

	@PutMapping("/{postId}")
	public void updatePost(@PathVariable Long shopId, @PathVariable String postId) {
	}

	@GetMapping
	public void listShopNews(@PathVariable Long shopId) {
	}

	@GetMapping("/{postId}")
	public void viewShopNews(@PathVariable Long shopId, @PathVariable Long postId) {
	}

	@DeleteMapping("/{postId}")
	public void deletePost(@PathVariable Long shopId, @PathVariable Long postId) {
	}

	@PostMapping("/{postId}/comments")
	public void registerComment(@PathVariable Long shopId, @PathVariable Long postId) {
	}

	@PutMapping("/{postId}/comments/{commentId}")
	public void updateComment(@PathVariable Long shopId, @PathVariable Long postId, @PathVariable Long commentId) {
	}

	@DeleteMapping("/{postId}/comments//{commentId}")
	public void deleteComment(@PathVariable Long shopId, @PathVariable Long postId, @PathVariable Long commentId) {
	}
}
