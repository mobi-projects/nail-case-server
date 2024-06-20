package com.nailcase.model.dto;

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
	}
}
