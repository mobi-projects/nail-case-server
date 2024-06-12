package com.nailcase.model.entity.review.dto;

import java.time.LocalDateTime;

import com.nailcase.model.entity.review.Review;
import com.nailcase.util.DateUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewDto {

	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private Long reviewId;
		private Long shopId;
		private Long memberId;
		private Long shopMemberId;
		private String title;
		private Double rating;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long reviewId;
		private Long shopId;
		private Long memberId;
		private Long shopMemberId;
		private String title;
		private Double rating;
		private Long createdAt;
		private String createdBy;
		private Long modifiedAt;
		private String modifiedBy;

		public void setTimestampsFromLocalDateTime(LocalDateTime createdAt, LocalDateTime modifiedAt) {
			this.createdAt = DateUtils.localDateTimeToUnixTimeStamp(createdAt);
			this.modifiedAt = DateUtils.localDateTimeToUnixTimeStamp(modifiedAt);
		}

		public static Response from(Review review) {
			Response response = new Response();
			response.setReviewId(review.getReviewId());
			response.setShopId(review.getShop().getShopId());
			response.setMemberId(review.getMember().getMember_id());
			response.setShopMemberId(review.getShopMember().getShopMemberId());
			response.setTitle(review.getTitle());
			response.setRating(review.getRating());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(review.getCreatedAt()));
			response.setCreatedBy(review.getCreatedBy());
			response.setModifiedAt(DateUtils.localDateTimeToUnixTimeStamp(review.getModifiedAt()));
			response.setModifiedBy(review.getModifiedBy());

			return response;
		}
	}
}