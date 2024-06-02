package com.nailcase.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.request.CreateCustomerRequest;
import com.nailcase.customer.domain.dto.request.UpdateCustomerRequest;
import com.nailcase.customer.domain.dto.response.CreateCustomerResponse;
import com.nailcase.customer.domain.dto.response.UpdateCustomerResponse;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;

	public Customer getCustomerById(Long id) {
		return customerRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
	}

	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	public CreateCustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
		Customer customer = Customer.builder()
			.name(createCustomerRequest.getName())
			.email(createCustomerRequest.getEmail())
			.phone(createCustomerRequest.getPhone())
			.createdBy(createCustomerRequest.getCreatedBy())
			.modifiedBy(createCustomerRequest.getModifiedBy())
			.build();

		Customer savedCustomer = customerRepository.save(customer);

		return new CreateCustomerResponse(
			savedCustomer.getCustomerId(),
			savedCustomer.getName(),
			savedCustomer.getEmail(),
			savedCustomer.getPhone(),
			savedCustomer.getCreatedAt(),
			savedCustomer.getModifiedAt(),
			savedCustomer.getCreatedBy(),
			savedCustomer.getModifiedBy()
		);
	}

	public UpdateCustomerResponse updateCustomer(Long id, UpdateCustomerRequest updateCustomerRequest) {
		Customer customer = customerRepository.findById(id)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		customer.updatePhone(updateCustomerRequest.getPhone());
		customer.updateModifiedBy(updateCustomerRequest.getModifiedBy());
		customer.updateModifiedAt();
		Customer updatedCustomer = customerRepository.save(customer);

		return new UpdateCustomerResponse(
			updatedCustomer.getCustomerId(),
			updatedCustomer.getName(),
			updatedCustomer.getEmail(),
			updatedCustomer.getPhone(),
			updatedCustomer.getModifiedBy(),
			updatedCustomer.getModifiedAt()
		);
	}

	public void deleteCustomer(Long id) {
		Customer customer = getCustomerById(id);
		customerRepository.delete(customer);
	}
}
