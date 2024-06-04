package com.nailcase.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.customer.domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
