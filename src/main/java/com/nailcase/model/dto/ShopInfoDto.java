package com.nailcase.model.dto;

import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.BaseTimeDto;
import com.nailcase.util.Validation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class ShopInfoDto {

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Address {
		@NotBlank
		@Schema(description = "주소", example = "서울시 종로구")
		private String address;

		@NotBlank
		@Validation(ruleName = "point")
		@Schema(description = "위경도", example = "127.123456,38.123456")
		private String point;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Info {
		@Min(0)
		@Schema(description = "주차 가능 대수", example = "1")
		private Integer parkingLotCnt;

		@Min(1)
		@Schema(description = "동시 예약 가능 자리", example = "5")
		private Integer availableCnt;

		@Schema(description = "안내사항", example = "예약은 마감 1시간 전까지 가능합니다.")
		private String info;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Price {
		@Schema(description = "가격", example = "손젤 손 케어 + 원컬러 40,000원")
		private String price;

		@Schema(description = "가격표 이미지", example = "image/jpeg | image/png")
		private MultipartFile priceImg;
	}

	@Data
	@NoArgsConstructor
	public static class PriceResponse {
		@Schema(description = "가격", example = "손젤 손 케어 + 원컬러 40,000원")
		private String price;

		@Schema(description = "이미지 url", example = "일단은 없습니다.")
		private String imageUrl;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class Response extends BaseTimeDto {
		@Schema(description = "샵 아이디", example = "1")
		private Long shopId;

		@Schema(description = "주소", example = "서울시 종로구")
		private String address;

		@Schema(description = "위경도", example = "127.123456,38.123456")
		private String point;

		@Schema(description = "주차 가능 대수", example = "1")
		private Integer parkingLotCnt;

		@Schema(description = "동시 예약 가능 자리", example = "5")
		private Integer availableCnt;

		@Schema(description = "안내사항", example = "예약은 마감 1시간 전까지 가능합니다.")
		private String information;

		@Schema(description = "가격", example = "손젤 손 케어 + 원컬러 40,000원")
		private String price;

		@Schema(description = "이미지 url", example = "일단은 없습니다.")
		private String imageUrl;
	}
}
