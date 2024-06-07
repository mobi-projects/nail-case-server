package com.nailcase.customer.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateCustomerRequestDto {
	@NotNull
	private Long customerId;

	@Size(min = 1, max = 32)
	private String name;

	@Email
	@Size(max = 128)
	private String email;

	@Size(max = 32)
	private String phone;
}
