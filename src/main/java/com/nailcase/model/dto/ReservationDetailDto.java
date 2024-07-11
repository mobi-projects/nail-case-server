package com.nailcase.model.dto;

import java.util.List;

import com.nailcase.model.enums.RemoveOption;
import com.nailcase.model.enums.ReservationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class ReservationDetailDto {

	@Data
	public static class Post {

		private Long shopId;

		private Long nailArtistId;

		@NotNull
		private Long startTime;

		@NotNull
		private RemoveOption remove;

		@NotNull
		private Boolean extend;

		@NotNull
		private List<ConditionDto.Post> conditionList;

		@NotNull
		private List<TreatmentDto.Post> treatmentList;
	}

	@Data
	public static class Patch {

		private Long reservationDetailId;

		private Long nailArtistId;
	}

	@Data
	public static class Response {

		private Long reservationDetailId;

		// TODO: nailArtistId -> nailArtistName
		private Long nailArtistId;

		private RemoveOption remove;

		private Boolean extend;

		private ReservationStatus status;

		private Long startTime;

		private Long endTime;

		private List<ConditionDto.Response> conditionList;

		private List<TreatmentDto.Response> treatmentList;

		private Long createdAt;

		private Long modifiedAt;
	}
}
