package com.nailcase.customer.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nailcase.customer.domain.Customer;

import jakarta.persistence.criteria.Predicate;

public class CustomerSpecifications {
	public static Specification<Customer> CustomerSpecification(Customer customer) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (customer.getName() != null) {
				predicates.add(criteriaBuilder.like(root.get("name"), "%" + customer.getName() + "%"));
			}
			if (customer.getEmail() != null) {
				predicates.add(criteriaBuilder.like(root.get("email"), "%" + customer.getEmail() + "%"));
			}
			if (customer.getPhone() != null) {
				predicates.add(criteriaBuilder.like(root.get("phone"), "%" + customer.getPhone() + "%"));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
