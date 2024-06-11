package com.nailcase.model.entity.post.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.nailcase.model.entity.post.Post;
import com.nailcase.model.entity.post.PostImage;
import com.nailcase.model.entity.post.comment.dto.PostCommentDto;
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
		private String title;
		private Category category;
		private String contents;
		private List<String> imageUrls;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Response {
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
			response.setCommentCount((long)post.getPostComments().size());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(post.getCreatedAt()));

			List<PostCommentDto.Response> commentResponses = post.getPostComments().stream()
				.map(PostCommentDto.Response::from)
				.collect(Collectors.toList());
			response.setComments(commentResponses);

			List<String> imageUrls = post.getPostImages().stream()
				.map(PostImage::getUrl)
				.collect(Collectors.toList());
			response.setImageUrls(imageUrls);

			return response;
		}
	}
}
