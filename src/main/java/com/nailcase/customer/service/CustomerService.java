package com.nailcase.customer.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nailcase.customer.CustomerMapper;
import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CreateCustomerDto;
import com.nailcase.customer.domain.dto.UpdateCustomerDto;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;

	private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

	public Customer getCustomerById(Long id) {
		return customerRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}

	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	public CreateCustomerDto.Response createCustomer(CreateCustomerDto createCustomerRequest) throws Exception {
		if (customerRepository.findByEmail(createCustomerRequest.getEmail()).isPresent()) {
			throw new BusinessException(UserErrorCode.USER_ALREADY_EXISTS);
		}

		Customer customer = customerMapper.toEntity(createCustomerRequest);
		customer.passwordEncode(passwordEncoder);
		Customer savedCustomer = customerRepository.save(customer);
		return customerMapper.toCreateResponse(savedCustomer);
	}

	public UpdateCustomerDto.Response updateCustomer(Long id, UpdateCustomerDto updateCustomerDto) {
		Customer customer = customerRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		customer.updatePhone(updateCustomerDto.getPhone());
		customer.updateModifiedBy(updateCustomerDto.getModifiedBy());

		Customer updatedCustomer = customerRepository.save(customer);
		return customerMapper.toUpdateResponse(updatedCustomer);
	}

	public void deleteCustomer(Long id) {
		Customer customer = getCustomerById(id);
		customerRepository.delete(customer);
	}
}
