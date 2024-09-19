package com.nailcase.model.dto;

import com.nailcase.model.enums.ConditionOption;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class ConditionDto {
	@Data
	public static class Post {

		@NotNull
		private ConditionOption option;
	}

	@Data
	public static class Response {

		private Long conditionId;

		private ConditionOption option;
	}

	@Data
	public static class pageResponse {

		private ConditionOption option;
		
	}
}
