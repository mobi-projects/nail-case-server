package com.nailcase.customer.domain.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerDto {

	private Long customerId;

	@NotNull
	@Size(max = 32)
	private String phone;

	@NotNull
	private Long modifiedBy;

	public record Response(
		Long customerId,
		String name,
		String email,
		String phone,
		LocalDateTime createdAt,
		LocalDateTime modifiedAt,
		Long createdBy,
		Long modifiedBy) {

	}
}
