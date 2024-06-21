package com.nailcase.model.dto;

import java.util.List;

import com.nailcase.common.dto.BaseTimeDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class ShopDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Post {
		@NotBlank
		@Size(max = 128)
		private String shopName;

		@NotBlank
		@Size(max = 16)
		private String phone;

		private int availableSeats = 0;
	}

	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Patch {
		@NotBlank
		private String overview;

		private List<String> tagNames;
	}

	@Data
	@NoArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class Response extends BaseTimeDto {
		private Long shopId;

		private String ownerId;

		private String shopName;

		private String phone;

		private String overview;

		private String address;

		private String availableSeats;

		private List<String> tags;
	}
}
