package com.nailcase.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.nailcase.model.enums.RemoveOption;
import com.nailcase.model.enums.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class ReservationDto {
	@Data
	public static class Post {
		// 기존 ReservationDto.Post의 메서드를 유지
		@Setter
		@NotNull
		private Long shopId;

		@Getter
		@NotNull
		private Long startTime;

		@NotNull
		private RemoveOption remove;

		@NotNull
		private Boolean extend;

		@NotNull
		private List<ConditionDto.Post> conditionList;

		@NotNull
		private TreatmentDto.Post treatment;

	}

	@Data
	public static class Patch {

		private ReservationStatus status;

		private List<ReservationDetailDto.Patch> reservationDetailDtoList = new ArrayList<>();
	}

	@Data
	public static class Confirm {

		private ReservationDetailDto.Confirm reservationDetail;
	}

	@Data
	public static class Response {

		private Long reservationId;

		private ReservationDetailDto.Response reservationDetail;

		private Long createdAt;

		private Long modifiedAt;
	}

	@Data
	public static class RegisterResponse {
		private Long reservationId;

		private Long reservationDetailId;

		private RemoveOption remove;

		private Boolean extend;

		private ReservationStatus status;

		private Long startTime;

		private Long endTime;

		private List<ConditionDto.Response> conditionList;

		private TreatmentDto.Response treatment;

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
			private String shopImageUrl;
		}

		@Data
		public static class ReservationDetailInfo {
			private Long reservationDetailsId;
			private Long startTime;
			private Long endTime;
			private List<String> treatmentOptions;
			private String removeOption;
			private List<String> conditionOptions;
			private String status;
			private Integer estimatedPrice;

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
