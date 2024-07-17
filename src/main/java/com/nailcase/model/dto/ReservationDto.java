package com.nailcase.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.nailcase.model.enums.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class ReservationDto {
	@Data
	public static class Post {

		private Long shopId;

		@NotNull
		private List<ReservationDetailDto.Post> reservationDetailList = new ArrayList<>();

		public void setShopId(Long shopId) {
			this.shopId = shopId;
			reservationDetailList.forEach(reservationDetailDto -> reservationDetailDto.setShopId(shopId));
		}

		public Long getStartTime() {
			return reservationDetailList.getFirst().getStartTime();
		}

		public List<Long> getNailArtistIds() {
			return reservationDetailList.stream().map(ReservationDetailDto.Post::getNailArtistId)
				.filter(Objects::nonNull)
				.toList();
		}
	}

	@Data
	public static class Patch {

		private ReservationStatus status;

		private List<ReservationDetailDto.Patch> reservationDetailDtoList = new ArrayList<>();
	}

	@Data
	public static class Confirm {

		private List<ReservationDetailDto.Confirm> reservationDetailList = new ArrayList<>();
	}

	@Data
	public static class Response {

		private Long reservationId;

		private String nickname;

		private List<ReservationDetailDto.Response> reservationDetailList = new ArrayList<>();

		private Long createdAt;

		private Long modifiedAt;
	}

	@Data
	public static class Available {

		private Long startTime;

		private int availableSeats;

		private boolean enable;

		private List<NailArtistDto.Response> artists;
	}

	@Data
	public static class MainPageResponse {
		private Long reservationId;
		private ShopInfo shop;
		private List<ReservationDetailInfo> details;

		@Data
		public static class ShopInfo {
			private Long id;
			private String name;
		}

		@Data
		public static class ReservationDetailInfo {
			private Long reservationDetailsId;
			private Long startTime;
			private Long endTime;
			private List<String> treatmentOptions;
			private String removeOption;
			private List<String> conditionOptions;
			private boolean accompanied;
			private String status;

			@Override
			public boolean equals(Object o) {
				if (this == o)
					return true;
				if (o == null || getClass() != o.getClass())
					return false;
				ReservationDetailInfo that = (ReservationDetailInfo)o;
				return Objects.equals(reservationDetailsId, that.reservationDetailsId);
			}

			@Override
			public int hashCode() {
				return Objects.hash(reservationDetailsId);
			}
		}
	}

	@Data
	public static class CompletedReservationResponse {
		private Long reservationId;
		private ShopInfo shop;
		private Long startTime;

		@Data
		public static class ShopInfo {
			private Long id;
			private String name;
			private String image;
		}
	}

}
