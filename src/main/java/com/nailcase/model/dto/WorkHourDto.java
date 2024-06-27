package com.nailcase.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class WorkHourDto {

	@Data
	public static class Put {
		@NotNull
		private Long workHourId;

		private Boolean isOpen;

		private Long openTime;

		private Long closeTime;
	}

	@Data
	public static class Response {
	}
}
