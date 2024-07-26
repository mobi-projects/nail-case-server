package com.nailcase.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkHourDto {
	private Long workHourId;

	@NotNull
	private Integer dayOfWeek;

	@NotNull
	private Boolean isOpen;

	@NotNull
	private Long openTime;

	@NotNull
	private Long closeTime;

	@Data
	@NoArgsConstructor
	public static class Post {

		@NotNull
		private Integer dayOfWeek;

		@NotNull
		private Boolean isOpen;

		@NotNull
		private Long openTime;

		@NotNull
		private Long closeTime;

		@Builder
		public Post(Integer dayOfWeek, Boolean isOpen, Long openTime, Long closeTime) {
			this.dayOfWeek = dayOfWeek;
			this.isOpen = isOpen;
			this.openTime = openTime;
			this.closeTime = closeTime;
		}
	}

}