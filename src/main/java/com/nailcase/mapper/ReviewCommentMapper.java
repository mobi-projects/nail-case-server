package com.nailcase.mapper;

import org.mapstruct.Mapper;

import com.nailcase.model.dto.ReviewCommentDto;
import com.nailcase.model.entity.ReviewComment;

@Mapper(componentModel = "spring")
public interface ReviewCommentMapper {
	default ReviewCommentDto.Response toResponse(ReviewComment comment) {
		return ReviewCommentDto.Response.from(comment);
	}
}