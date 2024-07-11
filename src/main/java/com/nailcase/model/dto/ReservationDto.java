package com.nailcase.model.dto;

import java.util.ArrayList;
import java.util.List;

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
				.toList();
		}
	}

	@Data
	public static class Patch {

		private ReservationStatus status;

		private List<ReservationDetailDto.Patch> reservationDetailDtoList = new ArrayList<>();
	}

	@Data
	public static class Response {

		private Long reservationId;

		private List<ReservationDetailDto.Response> reservationDetailList = new ArrayList<>();

		private Long createdAt;

		private Long modifiedAt;
	}

	@Data
	public static class Available {

		private Long startTime;

		private int availableSeats;

		private List<NailArtistDto.Response> artists;
	}
}
