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

import com.nailcase.customer.domain.dto.CustomerDto;
import com.nailcase.customer.service.CustomerService;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
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
	public ResponseEntity<SingleResponse<CustomerDto.Response>> getCustomerById(@PathVariable("id") Long id) {
		CustomerDto.Response res = customerService.getCustomer(id);
		SingleResponse<CustomerDto.Response> singleResponse = responseService.getSingleResponse(res);
		return ResponseEntity.ok(singleResponse);
	}

	@GetMapping
	public ResponseEntity<ListResponse<CustomerDto.Response>> getAllCustomers() {
		List<CustomerDto.Response> customers = customerService.getAllCustomers();
		ListResponse<CustomerDto.Response> listResponse = responseService.getListResponse(customers);
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(listResponse);
	}

	@PostMapping("/signup")
	public ResponseEntity<SingleResponse<CustomerDto.Response>> createCustomer(
		@Valid @RequestBody CustomerDto.Request createCustomerRequest
	) {
		CustomerDto.Response response = customerService.createCustomer(createCustomerRequest);
		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}

	// 원래라면 id를 시큐리티에서 받아서 사용
	@PutMapping("/{id}")
	public ResponseEntity<SingleResponse<CustomerDto.Response>> updateCustomer(
		@PathVariable("id") Long id,
		@Valid @RequestBody CustomerDto.Request updateCustomerRequest
	) {
		if (!id.equals(updateCustomerRequest.getCustomerId())) {
			throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
		}

		CustomerDto.Response response = customerService.updateCustomer(id, updateCustomerRequest);
		return ResponseEntity.ok(responseService.getSingleResponse(response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
		customerService.deleteCustomer(id);
		return ResponseEntity.ok().build();
	}
}
