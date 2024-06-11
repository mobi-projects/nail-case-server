package com.nailcase.domain.post.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.nailcase.domain.post.Post;
import com.nailcase.domain.post.comment.dto.PostCommentDto;
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
		private String body;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Response {
		private String title;
		private Long likes;
		private Boolean liked;
		private Long createdAt;
		private String body;
		private List<PostCommentDto.Response> comments;

		public static Response from(Post post) {
			Response response = new Response();
			response.setTitle(post.getTitle());
			response.setLikes(post.getLikeCounts());
			response.setLiked(post.getLiked());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(post.getCreatedAt()));
			response.setBody(post.getBody());

			List<PostCommentDto.Response> commentResponses = post.getPostComments().stream()
				.map(PostCommentDto.Response::from)
				.collect(Collectors.toList());

			response.setComments(commentResponses);

			return response;
		}
	}
}

