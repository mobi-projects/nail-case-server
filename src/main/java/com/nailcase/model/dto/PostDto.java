package com.nailcase.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nailcase.model.entity.Post;
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
		private Long shopId;
		private Long memberId;
		private String title;
		private Category category;
		private String contents;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Response {
		private List<Long> imageIds = new ArrayList<>();
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
		private List<String> imageUrls = new ArrayList<>();
		private List<PostCommentDto.Response> comments = new ArrayList<>();

		public static Response from(Post post, Boolean liked) {
			Response response = new Response();
			response.setPostId(post.getPostId());
			response.setTitle(post.getTitle());
			response.setCategory(post.getCategory());
			response.setContents(post.getContents());
			response.setLikes(post.getLikes());
			response.setViews(post.getViews());
			response.setLiked(liked);
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(post.getCreatedAt()));
			response.setMemberId(post.getCreatedBy());

			response.setImageUrls(post.getPostImages().stream()
				.map(postImage -> postImage.getBucketName() + "/" + postImage.getObjectName())
				.collect(Collectors.toList()));
			response.setImageIds(post.getPostImages().stream()
				.map(PostImage::getImageId)
				.collect(Collectors.toList()));
			response.setComments(post.getPostComments().stream()
				.map(PostCommentDto.Response::from)
				.collect(Collectors.toList()));

			return response;
		}
	}

}