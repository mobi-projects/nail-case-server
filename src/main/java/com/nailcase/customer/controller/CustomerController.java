package com.nailcase.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nailcase.customer.domain.Customer;
import com.nailcase.repository.CustomerRepository;
import com.nailcase.response.ListResponse;
import com.nailcase.response.ResponseService;
import com.nailcase.response.SingleResponse;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/customers")
@Slf4j
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ResponseService responseService;

	@PostMapping("/new")
	public ResponseEntity<SingleResponse<Customer>> createCustomer(@RequestBody Customer customerDetails) {
		Customer customer = Customer.builder()
			.name(customerDetails.getName())
			.email(customerDetails.getEmail())
			.phone(customerDetails.getPhone())
			.createdBy(customerDetails.getCreatedBy())
			.build();
		Customer savedCustomer = customerRepository.save(customer);
		return ResponseEntity.ok(responseService.getSingleResponse(savedCustomer));
	}


}
