package com.nailcase.model.dto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Review;
import com.nailcase.model.entity.ReviewImage;
import com.nailcase.model.entity.Shop;
import com.nailcase.util.DateUtils;

import lombok.AccessLevel;
import lombok.Builder;
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
		private String contents;
		private Double rating;
	}

	@Data
	@Builder
	public static class Response {
		private Long reviewId;
		private Long shopId;
		private Long memberId;
		private String contents;
		private Double rating;
		private Long createdAt;
		private Long createdBy;
		private Long modifiedAt;
		private Long modifiedBy;
		private List<Long> imageIds;
		private List<String> imageUrls;
		private List<ReviewCommentDto.Response> comments;

		public static Response from(Review review) {
			return Response.builder()
				.reviewId(review.getReviewId())
				.shopId(Optional.ofNullable(review.getShop()).map(Shop::getShopId).orElse(null))
				.memberId(Optional.ofNullable(review.getMember()).map(Member::getMemberId).orElse(null))
				.contents(review.getContents())
				.rating(review.getRating())
				.createdAt(DateUtils.localDateTimeToUnixTimeStamp(review.getCreatedAt()))
				.modifiedAt(DateUtils.localDateTimeToUnixTimeStamp(review.getModifiedAt()))
				.imageIds(getImageIds(review))
				.imageUrls(getImageUrls(review))
				.comments(getCommentResponses(review))
				.build();
		}

		private static List<Long> getImageIds(Review review) {
			return review.getReviewImages().stream()
				.map(ReviewImage::getImageId)
				.collect(Collectors.toList());
		}

		private static List<String> getImageUrls(Review review) {
			return review.getReviewImages().stream()
				.map(image -> image.getBucketName() + "/" + image.getObjectName())
				.collect(Collectors.toList());
		}

		private static List<ReviewCommentDto.Response> getCommentResponses(Review review) {
			return review.getReviewComments().stream()
				.map(ReviewCommentDto.Response::from)
				.collect(Collectors.toList());
		}
	}
}