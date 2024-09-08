package com.nailcase.model.dto;

import java.util.List;

import com.nailcase.model.enums.ConditionOption;
import com.nailcase.model.enums.TreatmentOption;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReviewDto {

	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private List<Long> imageIds;
		private Long reviewId;
		private Long shopId;
		private Long memberId;
		private Long shopMemberId;
		private String contents;
		private Double rating;
		private Long reservationDetailId;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long reviewId;
		private Long shopId;
		private Long memberId;
		private String nickname;
		private String contents;
		private Double rating;
		private TreatmentOption treatmentOptions;
		private List<ConditionOption> conditionOptions;
		private Integer visitCount;
		private Long createdAt;
		private Long createdBy;
		private Long modifiedAt;
		private Long modifiedBy;
		private List<Long> imageIds;
		private List<String> imageUrls;
		private List<ReviewCommentDto.Response> comments;

	}

}