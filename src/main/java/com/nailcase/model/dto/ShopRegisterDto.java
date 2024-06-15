package com.nailcase.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ShopRegisterDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		@NotBlank
		private String shopName;

		@NotBlank
		private String phone;

		private String overview;
	}

	@Data
	public static class Response {
		private Long shopId;
	}
}
