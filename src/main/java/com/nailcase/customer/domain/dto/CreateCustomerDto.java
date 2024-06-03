package com.nailcase.customer.domain.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerDto {
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

	@NotNull
	private Long createdBy;

	@NotNull
	private Long modifiedBy;

	public record Response(
		Long customerId,
		String name,
		String email,
		String phone,
		LocalDateTime createdAt,
		LocalDateTime modifiedAt,
		Long createdBy, Long modifiedBy) {
	}
}
