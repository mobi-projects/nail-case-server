package com.nailcase.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ImageDto {
	private Long id;
	private String bucketName;
	private String objectName;
	private String url;
	private Long createdBy;
	private Long modifiedBy;
}