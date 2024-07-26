package com.nailcase.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.BaseTimeDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class ShopDto {
	@Data
	@NoArgsConstructor
	public static class Post {
		@NotBlank
		@Size(min = 1, max = 20)
		private String shopName;

		@NotBlank
		@Size(min = 5, max = 50)
		private String address;

		@NotBlank
		@Size(max = 16)
		private String phone;

		@NotNull
		@Size(min = 1, max = 5)
		private List<MultipartFile> profileImages;

		@NotNull
		private List<WorkHourDto.Post> workHours;

		@NotNull
		@Size(min = 1, max = 5)
		private List<MultipartFile> priceImages;
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

		private int availableSeats;

		private Double shopAvgRatings;

		private List<String> tags;

		private List<Image> profileImages;

		private List<WorkHourDto.Post> workHours;

		private List<Image> priceImages;

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Image {
		private Long imageId;

		private String imageUrl;
	}

	@Data
	public static class MainPageResponse {
		private Long id;
		private String name;
		private String overview;
		private boolean likedByUser;  // 사용자가 매장을 좋아요 했는지 여부
	}
}
