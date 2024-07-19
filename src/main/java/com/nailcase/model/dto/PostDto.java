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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private List<Long> imageIds; // 이미지 ID 목록
		private Long shopId;
		private Long memberId;
		private String title;
		private Category category;
		private String contents;
	}

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Response {
		private List<Long> imageIds;
		private Long memberId;
		private Long shopId;
		private Long postId;
		private String title;
		private Category category;
		private String contents;
		private Long likes;
		private Boolean liked;
		private Long commentCount;
		private Long createdAt;
		private List<String> imageUrls;
		private List<PostCommentDto.Response> comments;
		private Long currentViewCount; // 추가된 필드

		public static Response from(Post post, Boolean liked) {
			return from(post, liked, null);
		}

		public static Response from(Post post, Boolean liked, Long currentViewCount) {
			Response response = Response.builder()
				.postId(post.getPostId())
				.title(post.getTitle())
				.category(post.getCategory())
				.contents(post.getContents())
				.likes(post.getLikes())
				.liked(liked)
				.currentViewCount(currentViewCount)
				.createdAt(DateUtils.localDateTimeToUnixTimeStamp(post.getCreatedAt()))
				.memberId(post.getCreatedBy())
				.currentViewCount(currentViewCount)
				.build();

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
				comments = new ArrayList<>();
			}

			List<PostCommentDto.Response> commentResponses = comments.stream()
				.map(PostCommentDto.Response::from)
				.collect(Collectors.toList());
			response.setComments(commentResponses);

			return response;
		}

	}
}