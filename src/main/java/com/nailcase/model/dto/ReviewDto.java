package com.nailcase.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nailcase.model.entity.Review;
import com.nailcase.model.entity.ReviewComment;
import com.nailcase.model.entity.ReviewImage;
import com.nailcase.util.DateUtils;

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
		private String contents;
		private Double rating;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
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
			Response response = new Response();
			response.setReviewId(review.getReviewId());
			response.setShopId(review.getShop().getShopId());
			response.setMemberId(review.getMember().getMemberId());
			response.setContents(review.getContents());
			response.setRating(review.getRating());
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(review.getCreatedAt()));
			response.setModifiedAt(DateUtils.localDateTimeToUnixTimeStamp(review.getModifiedAt()));

			List<ReviewImage> reviewImages = review.getReviewImages();
			if (reviewImages != null) {
				List<String> imageUrls = reviewImages.stream()
					.map(postImage -> postImage.getBucketName() + "/" + postImage.getObjectName())
					.collect(Collectors.toList());
				response.setImageUrls(imageUrls);
				List<Long> imageIds = reviewImages.stream()
					.map(ReviewImage::getImageId)
					.collect(Collectors.toList());
				response.setImageIds(imageIds);
			} else {
				response.setImageUrls(new ArrayList<>());
				response.setImageIds(new ArrayList<>());
			}

			List<ReviewComment> comments = review.getReviewComments();
			if (comments == null) {
				comments = new ArrayList<>(); // null일 경우 빈 리스트 할당
			}
			List<ReviewCommentDto.Response> commentResponses = comments.stream()
				.map(ReviewCommentDto.Response::from)
				.collect(Collectors.toList());
			response.setComments(commentResponses);

			return response;
		}
	}
}