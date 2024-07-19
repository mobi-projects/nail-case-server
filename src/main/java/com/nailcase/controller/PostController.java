package com.nailcase.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

import com.nailcase.annotation.ManagerOnly;
import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.NailArtistDetails;
import com.nailcase.model.dto.PostCommentDto;
import com.nailcase.model.dto.PostDto;
import com.nailcase.model.dto.PostImageDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shops/{shopId}/announcements")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@ManagerOnly
	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public CompletableFuture<List<PostImageDto>> uploadImages(
		@RequestParam("files") List<MultipartFile> files,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails,
		@PathVariable Long shopId) {
		log.info("Uploading images for shopId: {}", shopId);
		return postService.uploadImages(files, nailArtistDetails, shopId);
	}

	@ManagerOnly
	@PostMapping("/{announcementId}/images")
	@ResponseStatus(HttpStatus.CREATED)
	public void addImageToPost(
		@PathVariable Long announcementId,
		@RequestParam("files") List<MultipartFile> files,
		@PathVariable Long shopId,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails) {
		log.info("Adding images to post: {} for shopId: {} by {}", announcementId, shopId, nailArtistDetails);
		postService.addImageToPost(shopId, announcementId, files, nailArtistDetails);
	}

	@ManagerOnly
	@DeleteMapping("/{announcementId}/images/{imageId}")
	public void removeImageFromPost(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@PathVariable Long imageId,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails) {
		Long nailArtistId = nailArtistDetails.getNailArtistId();
		log.info("Removing image: {} from post: {} ,nailArtistId : {}", imageId, announcementId, nailArtistId);
		postService.removeImageFromPost(shopId, announcementId, imageId, nailArtistDetails);
	}

	@ManagerOnly
	@PostMapping
	public PostDto.Response registerPost(
		@PathVariable Long shopId,
		@RequestBody PostDto.Request postRequest,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails) {

		log.info("Registering new post for shopId: {},nailArtistId : {}", shopId, nailArtistDetails);
		return postService.registerPost(shopId, postRequest, nailArtistDetails);
	}

	@ManagerOnly
	@PutMapping("/{announcementId}")
	public CompletableFuture<PostDto.Response> updatePost(
		@PathVariable Long announcementId,
		@RequestBody PostDto.Request postRequest,
		@PathVariable Long shopId,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails) {
		log.info("Updating post: {} for shopId: {} by nailArtistId : {}", announcementId, shopId, nailArtistDetails);
		return postService.updatePost(shopId, announcementId, postRequest, nailArtistDetails);
	}

	@GetMapping
	public List<PostDto.Response> listShopNews(
		@PathVariable Long shopId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		log.info("Listing all posts for shopId: {}", shopId);
		return postService.listShopNews(shopId, userPrincipal);
	}

	@GetMapping("/{announcementId}")
	public PostDto.Response viewShopNews(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {
		log.info("Viewing post: {} for shopId: {}", announcementId, shopId);
		return postService.viewShopNews(shopId, announcementId, userPrincipal);
	}

	@DeleteMapping("/{announcementId}")
	public void deletePost(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@AuthenticationPrincipal NailArtistDetails nailArtistDetails) {
		Long nailArtistId = nailArtistDetails.getNailArtistId();
		log.info("Deleting post: {} for shopId: {}", announcementId, shopId);
		postService.deletePost(shopId, announcementId, nailArtistDetails);
	}

	@PostMapping("/{announcementId}/comments")
	public PostCommentDto.Response registerComment(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@RequestBody PostCommentDto.Request commentRequest,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		log.info("Registering comment for post: {} by memberId: {}", announcementId, memberId);
		return postService.registerComment(shopId, announcementId, commentRequest, memberId);
	}

	@PutMapping("/{announcementId}/comments/{commentId}")
	public PostCommentDto.Response updateComment(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@PathVariable Long commentId,
		@RequestBody PostCommentDto.Request commentRequest,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		log.info("Updating comment: {} for post: {} by memberId: {}", commentId, announcementId, memberId);
		return postService.updateComment(commentId, commentRequest, memberId);
	}

	@DeleteMapping("/{announcementId}/comments/{commentId}")
	public void deleteComment(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		log.info("Deleting comment: {} from post: {} by {}", commentId, announcementId, memberId);
		postService.deleteComment(shopId, announcementId, commentId, memberId);
	}

	@PostMapping("/{announcementId}/toggle-like")
	public boolean toggleLikePost(
		@PathVariable Long shopId,
		@PathVariable Long announcementId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getId();
		log.info("toggleLike post: {} for shopId: {}", memberId, shopId);
		return postService.toggleLike(shopId, announcementId, memberId);
	}
}