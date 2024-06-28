package com.nailcase.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorkHourDto {

	@NotNull
	private Long workHourId;

	private Boolean isOpen;

	private Long openTime;

	private Long closeTime;
}
