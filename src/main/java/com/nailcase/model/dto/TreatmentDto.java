package com.nailcase.model.dto;

import com.nailcase.model.enums.TreatmentOption;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class TreatmentDto {
	@Data
	public static class Post {

		@NotNull
		private TreatmentOption option;

		private Long imageId;

	}

	@Data
	public static class Response {

		private TreatmentOption option;

		private Long imageId;

		private String imageUrl;
	}
}
