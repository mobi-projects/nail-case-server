package com.nailcase.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
	@NoArgsConstructor
	public static class PostRequest {
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
		private List<WorkHourDto.Post> workHours;
	}

	@Data
	@NoArgsConstructor
	public static class PostResponse {
		private PostRequest requestData;
		private List<MultipartFile> profileImages;
		private List<MultipartFile> priceImages;

		public PostResponse(PostRequest requestData, List<MultipartFile> profileImages,
			List<MultipartFile> priceImages) {
			this.requestData = requestData;
			this.profileImages = profileImages;
			this.priceImages = priceImages;
		}
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
	public static class Response {

		private String shopName;

		private String phone;

		private String address;

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

	@Builder
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MainPageResponse {
		private Long shopId;
		private String shopName;
		private boolean likedByUser;  // 사용자가 매장을 좋아요 했는지 여부
	}

	@Builder
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InfiniteScrollResponse {
		private List<MainPageResponse> shopList;
		private int pageNumber;
		private int pageSize;
		private long totalElements;
		private int totalPages;
		private boolean last;
	}

}
