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
		@AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long shopId) {
		Long memberId = memberDetails.getMemberId();
		log.info("Uploading images for shopId: {}", shopId);
		return postService.uploadImages(files, memberId);
	}
  
	@PostMapping("/{announcementId}/images")
	@ResponseStatus(HttpStatus.CREATED)
	public void addImageToPost(@PathVariable Long announcementId, @RequestParam("files") List<MultipartFile> files,
		@PathVariable String shopId) {
		log.info("Adding images to post: {} for shopId: {}", announcementId, shopId);
		postService.addImageToPost(announcementId, files);
	}

	@DeleteMapping("/{announcementId}/images/{imageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeImageFromPost(@PathVariable Long announcementId, @PathVariable Long imageId,
		@PathVariable String shopId) {
		log.info("Removing image: {} from post: {}", imageId, announcementId);
		postService.removeImageFromPost(announcementId, imageId);
	}


	@PostMapping
	public PostDto.Response registerPost(@PathVariable Long shopId, @RequestBody PostDto.Request postRequest) {
		log.info("Registering new post for shopId: {}", shopId);
		return postService.registerPost(shopId, postRequest);
	}

	@PutMapping("/{announcementId}")
	public PostDto.Response updatePost(@PathVariable Long announcementId, @RequestBody PostDto.Request postRequest,
		@PathVariable Long shopId, @AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Updating post: {} for shopId: {}", announcementId, shopId);
		Long memberId = memberDetails.getMemberId();
		return postService.updatePost(shopId, announcementId, postRequest, memberId);
	}

	@GetMapping
	public List<PostDto.Response> listShopNews(@PathVariable Long shopId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		log.info("Listing all posts for shopId: {}", shopId);
		return postService.listShopNews(shopId, memberId);
	}

	@GetMapping("/{announcementId}")
	public PostDto.Response viewShopNews(@PathVariable Long announcementId, @PathVariable Long shopId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Viewing post: {} for shopId: {}", announcementId, shopId);
		Long memberId = memberDetails.getMemberId();
		return postService.viewShopNews(shopId, announcementId, memberId);
	}

	@DeleteMapping("/{announcementId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePost(@PathVariable Long shopId, @PathVariable Long announcementId) {
		log.info("Deleting post: {} for shopId: {}", announcementId, shopId);
		postService.deletePost(shopId, announcementId);
	}

	@PostMapping("/{announcementId}/comments")
	public PostCommentDto.Response registerComment(@PathVariable Long shopId, @PathVariable Long announcementId,
		@RequestBody PostCommentDto.Request commentRequest, @AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		log.info("Registering comment for post: {} by memberId: {}", announcementId, memberId);
		return postService.registerComment(shopId, announcementId, commentRequest, memberId);
	}

	@PutMapping("/{announcementId}/comments/{commentId}")
	public PostCommentDto.Response updateComment(@PathVariable Long shopId, @PathVariable Long announcementId,
		@PathVariable Long commentId, @RequestBody PostCommentDto.Request commentRequest) {
		log.info("Updating comment: {} for post: {}", commentId, announcementId);
		return postService.updateComment(commentId, commentRequest);
	}

	@DeleteMapping("/{announcementId}/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteComment(@PathVariable Long shopId, @PathVariable Long announcementId,
		@PathVariable Long commentId) {
		log.info("Deleting comment: {} from post: {}", commentId, announcementId);
		postService.deleteComment(shopId, announcementId, commentId);
	}

	@PostMapping("/{announcementId}/like")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void likePost(@PathVariable Long announcementId, @PathVariable Long shopId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Liking post: {} for shopId: {}", announcementId, shopId);
		Long memberId = memberDetails.getMemberId();
		postService.likePost(announcementId, memberId);
	}

	@PostMapping("/{announcementId}/unlike")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unLikePost(@PathVariable Long announcementId, @PathVariable Long shopId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		Long memberId = memberDetails.getMemberId();
		postService.unlikePost(announcementId, memberId);
	}
}