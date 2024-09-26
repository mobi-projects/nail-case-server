package com.nailcase.model.dto;

import java.util.List;

import com.nailcase.model.enums.RemoveOption;

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

}
