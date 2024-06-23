package com.nailcase.model.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.nailcase.model.entity.Post;
import com.nailcase.model.entity.PostImage;
import com.nailcase.model.enums.Category;
import com.nailcase.util.DateUtils;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private List<Long> imageIds;
		private Long shopId;
		private Long memberId;
		private String title;
		private Category category;
		private String contents;
	}

	@Data
	@Builder
	public static class Response {
		private List<Long> imageIds;
		private Long memberId;
		private Long shopId;
		private Long postId;
		private String title;
		private Category category;
		private String contents;
		private Long likes;
		private Long views;
		private Boolean liked;
		private Long commentCount;
		private Long createdAt;
		private List<String> imageUrls;
		private List<PostCommentDto.Response> comments;

		public static Response from(Post post, Boolean liked) {
			return Response.builder()
				.postId(post.getPostId())
				.title(post.getTitle())
				.category(post.getCategory())
				.contents(post.getContents())
				.likes(post.getLikes())
				.views(post.getViews())
				.liked(liked)
				.createdAt(DateUtils.localDateTimeToUnixTimeStamp(post.getCreatedAt()))
				.memberId(post.getCreatedBy())
				.imageUrls(getImageUrls(post))
				.imageIds(getImageIds(post))
				.comments(getComments(post))
				.build();
		}

		private static List<String> getImageUrls(Post post) {
			return post.getPostImages().stream()
				.map(postImage -> postImage.getBucketName() + "/" + postImage.getObjectName())
				.collect(Collectors.toList());
		}

		private static List<Long> getImageIds(Post post) {
			return post.getPostImages().stream()
				.map(PostImage::getImageId)
				.collect(Collectors.toList());
		}

		private static List<PostCommentDto.Response> getComments(Post post) {
			return post.getPostComments().stream()
				.map(PostCommentDto.Response::from)
				.collect(Collectors.toList());
		}
	}
}