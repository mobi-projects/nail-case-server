package com.nailcase.customer.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerRequest {

	private Long customerId;

	@NotNull
	@Size(max = 32)
	private String phone;

	@NotNull
	private Long modifiedBy;

}
