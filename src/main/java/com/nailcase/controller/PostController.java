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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.entity.post.comment.dto.PostCommentDto;
import com.nailcase.model.entity.post.dto.PostDto;
import com.nailcase.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/posts")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PostDto.Response registerPost(@PathVariable Long shopId, @RequestBody PostDto.Request postRequest) {
		return postService.registerPost(shopId, postRequest);
	}

	@PutMapping("/{postId}")
	public PostDto.Response updatePost(@PathVariable Long postId, @RequestBody PostDto.Request postRequest,
		@PathVariable Long shopId) {
		return postService.updatePost(shopId, postId, postRequest);
	}

	@GetMapping
	public List<PostDto.Response> listShopNews(@PathVariable Long shopId) {
		return postService.listShopNews(shopId);
	}

	@GetMapping("/{postId}")
	public PostDto.Response viewShopNews(@PathVariable Long postId, @PathVariable Long shopId,
		@RequestParam Long memberId) {
		return postService.viewShopNews(shopId, postId, memberId);
	}

	@DeleteMapping("/{postId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePost(@PathVariable Long shopId, @PathVariable Long postId) {
		postService.deletePost(shopId, postId);
	}

	@PostMapping("/{postId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public PostCommentDto.Response registerComment(@PathVariable Long shopId, @PathVariable Long postId,
		@RequestBody PostCommentDto.Request commentRequest) {
		return postService.registerComment(shopId, postId, commentRequest);
	}

	@PutMapping("/{postId}/comments/{commentId}")
	public PostCommentDto.Response updateComment(@PathVariable Long shopId, @PathVariable Long postId,
		@PathVariable Long commentId, @RequestBody PostCommentDto.Request commentRequest) {
		return postService.updateComment(commentId, commentRequest);
	}

	@DeleteMapping("/{postId}/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteComment(@PathVariable Long shopId, @PathVariable Long postId, @PathVariable Long commentId) {
		postService.deleteComment(shopId, postId, commentId);
	}

	@PostMapping("/{postId}/like")
	public void likePost(@PathVariable Long postId, @RequestParam Long memberId) {
		postService.likePost(postId, memberId);
	}
}
