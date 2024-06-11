package com.nailcase.customer.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.common.dto.PageRequestDto;
import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CustomerDto;
import com.nailcase.customer.domain.dto.UpdateCustomerRequestDto;
import com.nailcase.customer.service.CustomerService;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.oauth2.CustomOAuth2User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

	private final CustomerService customerService;

	@GetMapping("/auth-test")
	public CustomOAuth2User authTest(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
		return customOAuth2User;
	}

	@GetMapping("/{id}")
	public CustomerDto.Response getCustomerById(@PathVariable("id") Long id) {
		return customerService.getCustomer(id);
	}

	@GetMapping
	public Page<CustomerDto.Response> getAllCustomers(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String phone
	) {
		Pageable pageable = PageRequestDto.builder()
			.page(page)
			.sort("createdAt")
			.direction("DESC")
			.build().toPageable();
		Customer customer = Customer.builder()
			.name(name)
			.email(email)
			.phone(phone)
			.build();

		return customerService.getAllCustomers(customer, pageable);
	}

	@PostMapping("/signup")
	public CustomerDto.Response createCustomer(
		@Valid @RequestBody CustomerDto.Request createCustomerRequest
	) {
		return customerService.createCustomer(createCustomerRequest);
	}

	// 원래라면 id를 시큐리티에서 받아서 사용
	@PutMapping("/{id}")
	public CustomerDto.Response updateCustomer(
		@PathVariable("id") Long id,
		@Valid @RequestBody UpdateCustomerRequestDto updateCustomerRequest
	) {
		if (!id.equals(updateCustomerRequest.getCustomerId())) {
			throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
		}

		return customerService.updateCustomer(id, updateCustomerRequest);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
		customerService.deleteCustomer(id);
		return ResponseEntity.ok().build();
	}
}