package com.nailcase.model.dto;

import java.time.LocalDateTime;

import com.nailcase.model.entity.MonthlyArtComment;
import com.nailcase.util.DateUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MonthlyArtCommentDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private String body;
		private Long monthlyArtId;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long monthlyCommentId;
		private String body;
		private Long createdAt;
		private Long createdBy;

		public void setTimestampsFromLocalDateTime(LocalDateTime createdAt) {
			this.createdAt = DateUtils.localDateTimeToUnixTimeStamp(createdAt);
		}

		public static Response from(MonthlyArtComment monthlyArtComment) {
			Response response = new Response();
			response.setMonthlyCommentId(monthlyArtComment.getCommentId());
			response.setBody(monthlyArtComment.getBody());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(monthlyArtComment.getCreatedAt()));
			response.setCreatedBy(monthlyArtComment.getCreatedBy());

			return response;
		}
	}
}
