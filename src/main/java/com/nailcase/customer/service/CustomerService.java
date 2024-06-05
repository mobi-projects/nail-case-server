package com.nailcase.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.customer.CustomerMapper;
import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CustomerDto;
import com.nailcase.customer.domain.dto.UpdateCustomerRequestDto;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.customer.specifications.CustomerSpecifications;
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
	public Page<CustomerDto.Response> getAllCustomers(Customer customer, Pageable pageable) {
		Page<Customer> customerPage = customerRepository
			.findAll(CustomerSpecifications.CustomerSpecification(customer), pageable);

		return customerPage.map(customerMapper::toResponse);
	}

	@Transactional
	public CustomerDto.Response createCustomer(CustomerDto.Request createCustomerRequest) {
		Customer customer = customerMapper.toEntity(createCustomerRequest);
		Customer savedCustomer = customerRepository.save(customer);
		return customerMapper.toResponse(savedCustomer);
	}

	@Transactional
	public CustomerDto.Response updateCustomer(Long id, UpdateCustomerRequestDto updateCustomerRequest) {
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
