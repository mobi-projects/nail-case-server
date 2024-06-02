package com.nailcase.customer.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.request.CreateCustomerRequest;
import com.nailcase.customer.domain.dto.request.UpdateCustomerRequest;
import com.nailcase.customer.domain.dto.response.CreateCustomerResponse;
import com.nailcase.customer.domain.dto.response.UpdateCustomerResponse;
import com.nailcase.customer.service.CustomerService;
import com.nailcase.response.ListResponse;
import com.nailcase.response.ResponseService;
import com.nailcase.response.SingleResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

	private final CustomerService customerService;
	private final ResponseService responseService;

	@GetMapping("/{id}")
	public ResponseEntity<SingleResponse<Customer>> getCustomerById(@PathVariable("id") Long id) {
		Customer customer = customerService.getCustomerById(id);
		SingleResponse<Customer> singleResponse = responseService.getSingleResponse(customer);
		return ResponseEntity.ok(singleResponse);
	}

	@GetMapping("/all")
	public ResponseEntity<ListResponse<Customer>> getAllCustomers() {
		List<Customer> customers = customerService.getAllCustomers();
		ListResponse<Customer> listResponse = responseService.getListResponse(customers);
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(listResponse);
	}

	@PostMapping("/signup")
	public ResponseEntity<SingleResponse<CreateCustomerResponse>> createCustomer(@Valid @RequestBody CreateCustomerRequest createCustomerRequest) {
		CreateCustomerResponse response = customerService.createCustomer(createCustomerRequest);
		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}

	@PutMapping("/edit/{id}")
	public ResponseEntity<SingleResponse<UpdateCustomerResponse>> updateCustomer(
		@PathVariable("id") Long id,
		@Valid @RequestBody UpdateCustomerRequest updateCustomerRequest) {

		UpdateCustomerResponse response = customerService.updateCustomer(id, updateCustomerRequest);
		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
		customerService.deleteCustomer(id);
		return ResponseEntity.ok().build();
	}
}
