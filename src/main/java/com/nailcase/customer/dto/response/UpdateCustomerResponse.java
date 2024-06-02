package com.nailcase.customer.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerResponse {
	private Long customerId;
	private String name;
	private String email;
	private String phone;
	private Long modifiedBy;
	private LocalDateTime modifiedAt;
}

