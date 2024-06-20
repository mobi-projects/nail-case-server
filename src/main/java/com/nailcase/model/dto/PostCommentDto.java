package com.nailcase.model.dto;

import java.time.LocalDateTime;

import com.nailcase.model.entity.PostComment;
import com.nailcase.util.DateUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostCommentDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private String body;
		private Long postId;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long postCommentId;
		private String body;
		private Long createdAt;
		private Long createdBy;

		public void setTimestampsFromLocalDateTime(LocalDateTime createdAt) {
			this.createdAt = DateUtils.localDateTimeToUnixTimeStamp(createdAt);
		}

		public static Response from(PostComment postComment) {
			Response response = new Response();
			response.setPostCommentId(postComment.getCommentId());
			response.setBody(postComment.getBody());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(postComment.getCreatedAt()));
			response.setCreatedBy(postComment.getCreatedBy());

			return response;
		}
	}
}