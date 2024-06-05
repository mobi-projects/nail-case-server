package com.nailcase.customer;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CustomerDto;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
	CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

	Customer toEntity(CustomerDto.Request customerDto);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	CustomerDto.Response toResponse(Customer customer);

	@BeforeMapping
	default void beforeMapping(Customer customer, @MappingTarget CustomerDto.Response dto) {
		dto.setTimestampsFromLocalDateTime(customer.getCreatedAt(), customer.getModifiedAt());
	}
}
