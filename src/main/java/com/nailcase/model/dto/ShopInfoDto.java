package com.nailcase.model.dto;

import com.nailcase.util.Validation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

public class ShopInfoDto {

	@Data
	@NoArgsConstructor
	public static class Address {
		@NonNull
		Long shopId;
		@Validation(ruleName = "point")
		Double[] point;
	}

	@Data
	@NoArgsConstructor
	public static class Info {
	}

	@Data
	@NoArgsConstructor
	public static class Price {
	}
}
