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

import com.nailcase.annotation.AuthenticatedManagerUser;
import com.nailcase.annotation.AuthenticatedUser;
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

	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public List<PostImageDto> uploadImages(
		@RequestParam("files") List<MultipartFile> files,
		@AuthenticatedManagerUser Long managerId,
		@PathVariable Long shopId) {
		log.info("Uploading images for shopId: {}", shopId);
		return postService.uploadImages(files, managerId);
	}

	@PostMapping("/{announcementId}/images")
	@ResponseStatus(HttpStatus.CREATED)
	public void addImageToPost(
		@PathVariable Long announcementId,
		@RequestParam("files") List<MultipartFile> files,
		@PathVariable Long shopId,
		@AuthenticatedManagerUser Long managerId) {
		log.info("Adding images to post: {} for shopId: {}", announcementId, shopId);
		postService.addImageToPost(announcementId, files);
	}

	@DeleteMapping("/{announcementId}/images/{imageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeImageFromPost(
		@PathVariable Long announcementId,
		@PathVariable Long imageId,
		@PathVariable Long shopId,
		@AuthenticatedManagerUser Long managerId) {
		log.info("Removing image: {} from post: {}", imageId, announcementId);
		postService.removeImageFromPost(announcementId, imageId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PostDto.Response registerPost(
		@PathVariable Long shopId,
		@RequestBody PostDto.Request postRequest,
		@AuthenticatedManagerUser Long managerId) {
		log.info("Registering new post for shopId: {}", shopId);
		return postService.registerPost(shopId, postRequest);
	}

	@PutMapping("/{announcementId}")
	public PostDto.Response updatePost(
		@PathVariable Long announcementId,
		@RequestBody PostDto.Request postRequest,
		@PathVariable Long shopId,
		@AuthenticatedManagerUser Long managerId) {
		log.info("Updating post: {} for shopId: {}", announcementId, shopId);
		return postService.updatePost(shopId, announcementId, postRequest, managerId);
	}

	@GetMapping
	public List<PostDto.Response> listShopNews(
		@PathVariable Long shopId,
		@AuthenticatedUser Long userId) {
		log.info("Listing all posts for shopId: {}", shopId);
		return postService.listShopNews(shopId, userId);
	}

	@GetMapping("/{announcementId}")
	public PostDto.Response viewShopNews(
		@PathVariable Long announcementId,
		@PathVariable Long shopId,
		@AuthenticatedUser Long userId) {
		log.info("Viewing post: {} for shopId: {}", announcementId, shopId);
		return postService.viewShopNews(shopId, announcementId, userId);
	}

	@DeleteMapping("/{announcementId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePost(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@AuthenticatedManagerUser Long managerId) {
		log.info("Deleting post: {} for shopId: {}", announcementId, shopId);
		postService.deletePost(shopId, announcementId);
	}

	@PostMapping("/{announcementId}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public PostCommentDto.Response registerComment(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@RequestBody PostCommentDto.Request commentRequest,
		@AuthenticatedUser Long userId) {
		log.info("Registering comment for post: {} by userId: {}", announcementId, userId);
		return postService.registerComment(shopId, announcementId, commentRequest, userId);
	}

	@PutMapping("/{announcementId}/comments/{commentId}")
	public PostCommentDto.Response updateComment(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@PathVariable Long commentId,
		@RequestBody PostCommentDto.Request commentRequest,
		@AuthenticatedUser Long userId) {
		log.info("Updating comment: {} for post: {}", commentId, announcementId);
		return postService.updateComment(commentId, commentRequest, userId);
	}

	@DeleteMapping("/{announcementId}/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteComment(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@PathVariable Long commentId,
		@AuthenticatedUser Long userId) {
		log.info("Deleting comment: {} from post: {}", commentId, announcementId);
		postService.deleteComment(shopId, announcementId, commentId, userId);
	}

	@PostMapping("/{announcementId}/like")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void likePost(
		@PathVariable Long announcementId,
		@PathVariable Long shopId,
		@AuthenticatedUser Long userId) {
		log.info("Liking post: {} for shopId: {}", announcementId, shopId);
		postService.likePost(announcementId, userId);
	}

	@PostMapping("/{announcementId}/unlike")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unLikePost(
		@PathVariable Long announcementId,
		@PathVariable Long shopId,
		@AuthenticatedUser Long userId) {
		log.info("Unliking post: {} for shopId: {}", announcementId, shopId);
		postService.unlikePost(announcementId, userId);
	}
}