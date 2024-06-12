package com.nailcase.model.entity.review.comment.dto;

import java.time.LocalDateTime;

import com.nailcase.model.entity.review.comment.ReviewComment;
import com.nailcase.util.DateUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewCommentDto {

	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private Long reviewCommentId;
		private String contents;
		private Long reviewId;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long reviewCommentId;
		private String contents;
		private Long createdAt;
		private String createdBy;

		public void setTimestampsFromLocalDateTime(LocalDateTime createdAt) {
			this.createdAt = DateUtils.localDateTimeToUnixTimeStamp(createdAt);
		}

		public static Response from(ReviewComment reviewComment) {
			Response response = new Response();
			response.setReviewCommentId(reviewComment.getReviewCommentId());
			response.setContents(reviewComment.getContents());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(reviewComment.getCreatedAt()));
			response.setCreatedBy(reviewComment.getCreatedBy());

			return response;
		}
	}
}