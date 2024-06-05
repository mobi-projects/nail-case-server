package com.nailcase.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.customer.CustomerMapper;
import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CustomerDto;
import com.nailcase.customer.domain.dto.CustomerOptionalDto;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

	@Transactional(readOnly = true)
	public CustomerDto.Response getCustomer(Long id) {
		return customerMapper.toResponse(getCustomerById(id));
	}

	@Transactional(readOnly = true)
	public List<CustomerDto.Response> getAllCustomers() {
		return customerRepository
			.findAll()
			.stream()
			.map(customerMapper::toResponse)
			.toList();
	}

	@Transactional
	public CustomerDto.Response createCustomer(CustomerDto.Request createCustomerRequest) {
		Customer customer = customerMapper.toEntity(createCustomerRequest);
		Customer savedCustomer = customerRepository.save(customer);
		return customerMapper.toResponse(savedCustomer);
	}

	@Transactional
	public CustomerDto.Response updateCustomer(Long id, CustomerOptionalDto updateCustomerRequest) {
		Customer customer = getCustomerById(id);
		customer.update(updateCustomerRequest);
		Customer updatedCustomer = customerRepository.saveAndFlush(customer);
		return customerMapper.toResponse(updatedCustomer);
	}

	@Transactional
	public void deleteCustomer(Long id) {
		Customer customer = getCustomerById(id);
		customerRepository.delete(customer);
	}

	@Transactional(readOnly = true)
	protected Customer getCustomerById(Long id) {
		return customerRepository
			.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}
}
