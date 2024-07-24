package com.nailcase.model.dto;

import jakarta.validation.constraints.NotNull;
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
}