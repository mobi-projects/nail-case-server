package com.nailcase.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nailcase.model.entity.Post;
import com.nailcase.model.entity.PostComment;
import com.nailcase.model.entity.PostImage;
import com.nailcase.model.enums.Category;
import com.nailcase.util.DateUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private List<Long> imageIds; // 이미지 ID 목록
		// private Long shopId;
		private Long memberId;
		private String title;
		private Category category;
		private String contents;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Response {
		private List<Long> imageIds;
		private Long memberId;
		// private Long shopId;
		private Long postId;
		private String title;
		private Category category;
		private String contents;
		private Long likes;
		private Long views;
		private Long commentCount;
		private Boolean liked;
		private Long createdAt;
		private List<String> imageUrls;
		private List<PostCommentDto.Response> comments;

		public static Response from(Post post, Long viewCount) {
			Response response = new Response();
			response.setPostId(post.getPostId());
			response.setTitle(post.getTitle());
			response.setCategory(post.getCategory());
			response.setContents(post.getContents());
			response.setLikes(post.getLikes());
			response.setViews(viewCount != null ? viewCount : post.getViews());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(post.getCreatedAt()));

			response.setMemberId(post.getCreatedBy());

			List<PostImage> postImages = post.getPostImages();
			if (postImages != null) {
				List<String> imageUrls = postImages.stream()
					.map(postImage -> postImage.getBucketName() + "/" + postImage.getObjectName())
					.collect(Collectors.toList());
				response.setImageUrls(imageUrls);
				List<Long> imageIds = postImages.stream()
					.map(PostImage::getImageId)
					.collect(Collectors.toList());
				response.setImageIds(imageIds);
			} else {
				response.setImageUrls(new ArrayList<>());
				response.setImageIds(new ArrayList<>());
			}

			List<PostComment> comments = post.getPostComments();
			if (comments == null) {
				comments = new ArrayList<>(); // null일 경우 빈 리스트 할당
			}

			List<PostCommentDto.Response> commentResponses = comments.stream()
				.map(PostCommentDto.Response::from)
				.collect(Collectors.toList());
			response.setComments(commentResponses);

			return response;
		}

	}
}
