package com.nailcase.model.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.nailcase.model.entity.MonthlyArt;
import com.nailcase.model.entity.MonthlyArtComment;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.util.DateUtils;
import com.nailcase.util.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MonthlyArtDto {

	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		private List<Long> imageIds; // 이미지 ID 목록
		private Long shopId;
		private Long memberId;
		private String title;
		private String contents;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Response {
		private List<Long> imageIds;
		private Long memberId;
		private Long shopId;
		private Long monthlyArtId;
		private String title;
		private String contents;
		private Long likes;
		private Long views;
		private Boolean liked;
		private Long commentCount;
		private Long createdAt;
		private List<String> imageUrls;
		private List<MonthlyArtCommentDto.Response> comments;

		public static MonthlyArtDto.Response from(MonthlyArt monthlyArt, Boolean liked) {
			MonthlyArtDto.Response response = new MonthlyArtDto.Response();
			response.setMonthlyArtId(monthlyArt.getMonthlyArtId());
			response.setTitle(monthlyArt.getTitle());
			response.setContents(monthlyArt.getContents());
			response.setLikes(monthlyArt.getLikes());
			response.setViews(monthlyArt.getViews());
			response.setLiked(liked);
			response.setCreatedAt(DateUtils.localDateTimeToUnixTimeStamp(monthlyArt.getCreatedAt()));

			response.setMemberId(monthlyArt.getCreatedBy());

			List<MonthlyArtImage> monthlyArtImages = monthlyArt.getMonthlyArtImages();
			if (monthlyArtImages != null) {
				List<String> imageUrls = monthlyArtImages.stream()
					.map(postImage -> postImage.getBucketName() + "/" + postImage.getObjectName())
					.collect(Collectors.toList());
				response.setImageUrls(imageUrls);
				List<Long> imageIds = monthlyArtImages.stream()
					.map(MonthlyArtImage::getImageId)
					.collect(Collectors.toList());
				response.setImageIds(imageIds);
			} else {
				response.setImageUrls(new ArrayList<>());
				response.setImageIds(new ArrayList<>());
			}

			List<MonthlyArtComment> comments = monthlyArt.getMonthlyArtComments();
			if (comments == null) {
				comments = new ArrayList<>(); // null일 경우 빈 리스트 할당
			}

			List<MonthlyArtCommentDto.Response> commentResponses = comments.stream()
				.map(MonthlyArtCommentDto.Response::from)
				.collect(Collectors.toList());
			response.setComments(commentResponses);

			return response;
		}

	}

	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ImageDto {
		private Long imageId; // 이미지 ID 목록
		private String imageUrl;

		public static List<MonthlyArtDto.ImageDto> toImageResponse(MonthlyArt monthlyArt) {
			if (monthlyArt == null) {
				return Collections.emptyList();
			}
			return monthlyArt.getMonthlyArtImages().stream()
				.map(
					image ->
					{
						ImageDto imageDto = new ImageDto();
						imageDto.setImageId(image.getImageId());
						imageDto.setImageUrl(
							StringUtils.generateImageUrl(image.getBucketName(), image.getObjectName()));
						return imageDto;
					}).collect(Collectors.toList());
		}

	}

}
