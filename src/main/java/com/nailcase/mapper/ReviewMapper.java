package com.nailcase.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.nailcase.model.dto.ReviewDto;
import com.nailcase.model.entity.Condition;
import com.nailcase.model.entity.Review;
import com.nailcase.model.entity.ReviewImage;
import com.nailcase.model.entity.Treatment;
import com.nailcase.model.enums.ConditionOption;
import com.nailcase.model.enums.TreatmentOption;

@Mapper(componentModel = "spring", uses = {ReviewCommentMapper.class})
public interface ReviewMapper {

	@Mapping(target = "shopId", source = "shop.shopId")
	@Mapping(target = "memberId", source = "member.memberId")
	@Mapping(target = "nickname", source = "member.nickname")
	@Mapping(target = "accompaniedIn", expression = "java(review.getReservationDetail().getReservation().isAccompanied())")
	@Mapping(target = "treatmentOptions", expression = "java(mapTreatmentOptions(review))")
	@Mapping(target = "conditionOptions", expression = "java(mapConditionOptions(review))")
	@Mapping(target = "imageIds", ignore = true)
	@Mapping(target = "imageUrls", ignore = true)
	@Mapping(target = "comments", source = "reviewComments")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	ReviewDto.Response toResponse(Review review);

	@AfterMapping
	default void setImageInfo(@MappingTarget ReviewDto.Response response, Review review) {
		List<ReviewImage> reviewImages = review.getReviewImages();
		if (reviewImages != null && !reviewImages.isEmpty()) {
			response.setImageUrls(reviewImages.stream()
				.map(image -> image.getBucketName() + "/" + image.getObjectName())
				.collect(Collectors.toList()));
			response.setImageIds(reviewImages.stream()
				.map(ReviewImage::getImageId)
				.collect(Collectors.toList()));
		} else {
			response.setImageUrls(new java.util.ArrayList<>());
			response.setImageIds(new java.util.ArrayList<>());
		}
	}

	default List<TreatmentOption> mapTreatmentOptions(Review review) {
		return review.getReservationDetail().getTreatmentList().stream()
			.map(Treatment::getOption)
			.collect(Collectors.toList());
	}

	default List<ConditionOption> mapConditionOptions(Review review) {
		return review.getReservationDetail().getConditionList().stream()
			.map(Condition::getOption)
			.collect(Collectors.toList());
	}

	// @Named("localDateTimeToLong")
	// default Long mapLocalDateTimeToLong(LocalDateTime dateTime) {
	// 	return dateTime == null ? null : dateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
	// }

	List<ReviewDto.Response> toResponseList(List<Review> reviews);
}