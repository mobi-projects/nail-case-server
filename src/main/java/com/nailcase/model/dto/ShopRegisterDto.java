package com.nailcase.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ShopRegisterDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		@NotBlank
		@Size(max = 128)
		private String shopName;

		@NotBlank
		@Size(max = 16)
		private String phone;

		private String overview;
	}

	@Data
	@NoArgsConstructor
	public static class Response {
		private Long shopId;
	}
}
