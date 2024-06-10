package com.nailcase.customer.repository;

import static com.nailcase.customer.domain.QCustomer.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.QCustomer;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomerQuerydslRepositoryImpl implements CustomerQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Customer> findByConditions(Customer customer, Pageable pageable) {
		List<Customer> customers = queryFactory.selectFrom(QCustomer.customer)
			.where(containsName(customer.getName()),
				containsEmail(customer.getEmail()),
				containsPhone(customer.getPhone()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(QCustomer.customer.count())
			.from(QCustomer.customer)
			.where(containsName(customer.getName()),
				containsEmail(customer.getEmail()),
				containsPhone(customer.getPhone()))
			.fetchOne();

		count = count == null ? 0L : count;

		return new PageImpl<>(customers, pageable, count);
	}

	private static BooleanExpression containsName(String name) {
		return name == null ? null : customer.name.contains(name);
	}

	private static BooleanExpression containsEmail(String email) {
		return email == null ? null : customer.email.contains(email);
	}

	private static BooleanExpression containsPhone(String phone) {
		return phone == null ? null : customer.phone.contains(phone);
	}
}
