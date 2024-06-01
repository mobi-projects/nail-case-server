package com.nailcase.repository;

import org.springframework.stereotype.Repository;

import com.nailcase.customer.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
