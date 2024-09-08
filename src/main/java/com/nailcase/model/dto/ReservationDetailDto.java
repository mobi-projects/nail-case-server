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

		private Long reservationDetailId;

		private Long nailArtistId;
	}

	@Data
	public static class Confirm {

		@NotNull
		private Long reservationDetailId;

		private Long startTime;

		@NotNull
		private Long endTime;
	}

	@Data
	public static class Response {

		private Long reservationDetailId;

		private RemoveOption remove;

		private Boolean extend;

		private ReservationStatus status;

		private Long startTime;

		private Long endTime;

		private List<ConditionDto.Response> conditionList;

		private TreatmentDto.Response treatment;

	}
}
