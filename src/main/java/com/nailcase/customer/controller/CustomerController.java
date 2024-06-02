package com.nailcase.customer.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.dto.request.CreateCustomerRequest;
import com.nailcase.customer.dto.request.UpdateCustomerRequest;
import com.nailcase.customer.dto.response.CreateCustomerResponse;
import com.nailcase.customer.dto.response.UpdateCustomerResponse;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
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

	private final CustomerRepository customerRepository;
	private final ResponseService responseService;



	@GetMapping("/{id}")
	public ResponseEntity<SingleResponse<Customer>> getCustomerById(@PathVariable("id") Long id) {
		Customer customer = customerRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		SingleResponse<Customer> singleResponse = responseService.getSingleResponse(customer);
		return ResponseEntity.ok(singleResponse);
	}


	@GetMapping("/all")
	public ResponseEntity<ListResponse<Customer>> getAllCustomers() {
		List<Customer> customers = customerRepository.findAll();
		ListResponse<Customer> listResponse = responseService.getListResponse(customers);
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(listResponse);
	}




	@PostMapping("/signup")
	public ResponseEntity<SingleResponse<CreateCustomerResponse>> createCustomer(@Valid @RequestBody CreateCustomerRequest createCustomerRequest) {
		Customer customer = Customer.builder()
			.name(createCustomerRequest.getName())
			.email(createCustomerRequest.getEmail())
			.phone(createCustomerRequest.getPhone())
			.createdBy(createCustomerRequest.getCreatedBy())
			.modifiedBy(createCustomerRequest.getModifiedBy())
			.build();

		Customer savedCustomer = customerRepository.save(customer);

		CreateCustomerResponse response = new CreateCustomerResponse(
			savedCustomer.getCustomerId(),
			savedCustomer.getName(),
			savedCustomer.getEmail(),
			savedCustomer.getPhone(),
			savedCustomer.getCreatedAt(),
			savedCustomer.getModifiedAt(),
			savedCustomer.getCreatedBy(),
			savedCustomer.getModifiedBy()
		);

		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}

	@PutMapping("/edit/{id}")
	public ResponseEntity<SingleResponse<UpdateCustomerResponse>> updateCustomer(
		@PathVariable("id") Long id,
		@Valid @RequestBody UpdateCustomerRequest updateCustomerRequest) {

		Customer customer = customerRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		customer.updatePhone(updateCustomerRequest.getPhone());
		customer.updateModifiedBy(updateCustomerRequest.getModifiedBy());
		customer.updateModifiedAt();
		Customer updatedCustomer = customerRepository.save(customer);

		UpdateCustomerResponse response = new UpdateCustomerResponse(
			updatedCustomer.getCustomerId(),
			updatedCustomer.getName(),
			updatedCustomer.getEmail(),
			updatedCustomer.getPhone(),
			updatedCustomer.getModifiedBy(),
			updatedCustomer.getModifiedAt()
		);

		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}



	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
		customerRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		customerRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}

}
