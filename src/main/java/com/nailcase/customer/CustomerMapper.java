package com.nailcase.customer;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CreateCustomerDto;
import com.nailcase.customer.domain.dto.UpdateCustomerDto;

@Mapper
public interface CustomerMapper {
	CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

	Customer toEntity(CreateCustomerDto createCustomerDto);

	Customer toEntity(UpdateCustomerDto updateCustomerDto);

	CreateCustomerDto.Response toCreateResponse(Customer customer);

	UpdateCustomerDto.Response toUpdateResponse(Customer customer);
}
