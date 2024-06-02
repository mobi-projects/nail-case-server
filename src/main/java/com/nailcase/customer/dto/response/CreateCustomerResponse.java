package com.nailcase.customer.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerResponse {
	private Long customerId;
	private String name;
	private String email;
	private String phone;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	private Long createdBy;
	private Long modifiedBy;
}
