package com.nailcase.customer.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CustomerDto {
	@Data
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Request {
		@NotNull
		@Size(min = 1, max = 32)
		private String name;

		@NotNull
		@Email
		@Size(max = 128)
		private String email;

		@NotNull
		@Size(max = 32)
		private String phone;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Response {
		private Long customerId;

		private String name;

		private String email;

		private String phone;

		@Setter
		private Long createdAt;

		@Setter
		private Long modifiedAt;

		private Long createdBy;

		private Long modifiedBy;
	}
}
