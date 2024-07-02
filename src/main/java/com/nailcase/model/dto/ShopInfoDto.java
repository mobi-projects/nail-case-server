package com.nailcase.model.dto;

import com.nailcase.util.Validation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

public class ShopInfoDto {

	@Data
	@NoArgsConstructor
	@Schema(title = "주소 관련 객체")
	public static class Address {
		@NonNull
		@Schema(title = "샵 아이디")
		Long shopId;

		@NotBlank
		@Schema(title = "주소 ex) '서울시 종로구'")
		String address;

		@NotBlank
		@Validation(ruleName = "point")
		@Schema(title = "위경도 ex) '127,38'(경도,위도)")
		String point;
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
