package com.nailcase.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.customer.domain.Customer;

public interface CustomerQuerydslRepository {
	Page<Customer> findByConditions(Customer customer, Pageable pageable);
}
