package com.nailcase.customer.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CreateCustomerDto;
import com.nailcase.customer.domain.dto.UpdateCustomerDto;
import com.nailcase.customer.service.CustomerService;
import com.nailcase.response.ListResponse;
import com.nailcase.response.ResponseService;
import com.nailcase.response.SingleResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/customers")
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

	@GetMapping
	public ResponseEntity<ListResponse<Customer>> getAllCustomers() {
		List<Customer> customers = customerService.getAllCustomers();
		ListResponse<Customer> listResponse = responseService.getListResponse(customers);
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(listResponse);
	}

	@PostMapping("/signup")
	public ResponseEntity<SingleResponse<CreateCustomerDto.Response>> createCustomer(
		@Valid @RequestBody CreateCustomerDto createCustomerRequest) {
		CreateCustomerDto.Response response = customerService.createCustomer(createCustomerRequest);
		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}

	@PutMapping("/{id}")
	public ResponseEntity<SingleResponse<UpdateCustomerDto.Response>> updateCustomer(
		@PathVariable("id") Long id,
		@Valid @RequestBody UpdateCustomerDto updateCustomerDto) {

		UpdateCustomerDto.Response response = customerService.updateCustomer(id, updateCustomerDto);
		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
		customerService.deleteCustomer(id);
		return ResponseEntity.ok().build();
	}
}
