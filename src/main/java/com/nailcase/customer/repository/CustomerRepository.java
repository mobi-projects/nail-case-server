package com.nailcase.customer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.SocialType;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	Optional<Customer> findByEmail(String email);

	Optional<Customer> findByName(String name);
	
	Optional<Customer> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

}
