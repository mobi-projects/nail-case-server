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
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.model.dto.PostCommentDto;
import com.nailcase.model.dto.PostDto;
import com.nailcase.model.dto.PostImageDto;
import com.nailcase.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shops/{shopId}/announcements")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	// 이미지만 업로드하는 API
	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public List<PostImageDto> uploadImages(@RequestParam("files") List<MultipartFile> files,
		@PathVariable Long shopId,
		@RequestParam Long memberId) {
		return postService.uploadImages(files, memberId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PostDto.Response registerPost(@PathVariable Long shopId, @RequestBody PostDto.Request postRequest) {
		return postService.registerPost(shopId, postRequest);
	}

	@PutMapping("/{announcementId}")
	public PostDto.Response updatePost(@PathVariable Long announcementId, @RequestBody PostDto.Request postRequest,
		@PathVariable Long shopId) {
		return postService.updatePost(shopId, announcementId, postRequest);
	}

	@GetMapping
	public List<PostDto.Response> listShopNews(@PathVariable Long shopId) {
		return postService.listShopNews(shopId);
	}

	@GetMapping("/{announcementId}")
	public PostDto.Response viewShopNews(@PathVariable Long announcementId, @PathVariable Long shopId,
		@RequestParam Long memberId) {
		// @RequestHeader("Authorization") String authorizationHeader
		// Long memberId = extractMemberIdFromToken(authorizationHeader);
		return postService.viewShopNews(shopId, announcementId, memberId);
	}

	@DeleteMapping("/{announcementId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePost(@PathVariable Long shopId, @PathVariable Long announcementId) {
		postService.deletePost(shopId, announcementId);
	}

	@PostMapping("/{announcementId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public PostCommentDto.Response registerComment(@PathVariable Long shopId, @PathVariable Long announcementId,
		@RequestBody PostCommentDto.Request commentRequest) {
		return postService.registerComment(shopId, announcementId, commentRequest);
	}

	@PutMapping("/{announcementId}/comments/{commentId}")
	public PostCommentDto.Response updateComment(@PathVariable Long shopId, @PathVariable Long announcementId,
		@PathVariable Long commentId, @RequestBody PostCommentDto.Request commentRequest) {
		return postService.updateComment(commentId, commentRequest);
	}

	@DeleteMapping("/{announcementId}/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteComment(@PathVariable Long shopId, @PathVariable Long announcementId,
		@PathVariable Long commentId) {
		postService.deleteComment(shopId, announcementId, commentId);
	}

	@PostMapping("/{announcementId}/images")
	@ResponseStatus(HttpStatus.CREATED)
	public void addImageToPost(@PathVariable Long announcementId, @RequestParam("files") List<MultipartFile> files,
		@PathVariable String shopId) {
		postService.addImageToPost(announcementId, files);
	}

	@DeleteMapping("/{announcementId}/images/{imageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeImageFromPost(@PathVariable Long announcementId, @PathVariable Long imageId,
		@PathVariable String shopId) {
		postService.removeImageFromPost(announcementId, imageId);
	}

	@PostMapping("/{announcementId}/like")
	public void likePost(@PathVariable Long announcementId, @RequestParam Long memberId, @PathVariable String shopId) {
		postService.likePost(announcementId, memberId);
	}
}
