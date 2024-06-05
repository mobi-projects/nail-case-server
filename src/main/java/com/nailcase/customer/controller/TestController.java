package com.nailcase.customer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.customer.domain.dto.CreateCustomerDto;
import com.nailcase.customer.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

	private final CustomerService customerService;

	@PostMapping("/sign-up")
	public String signUp(@RequestBody CreateCustomerDto userSignUpDto) throws Exception {
		customerService.createCustomer(userSignUpDto);
		return "회원가입 성공";
	}

	@GetMapping("/jwt-test")
	public String jwtTest() {
		return "jwtTest 요청 성공";
	}
}
