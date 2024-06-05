package com.nailcase.customer;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CreateCustomerDto;
import com.nailcase.customer.domain.dto.UpdateCustomerDto;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-06T01:23:41+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toEntity(CreateCustomerDto createCustomerDto) {
        if ( createCustomerDto == null ) {
            return null;
        }

        Customer.CustomerBuilder<?, ?> customer = Customer.builder();

        customer.name( createCustomerDto.getName() );
        customer.email( createCustomerDto.getEmail() );
        customer.phone( createCustomerDto.getPhone() );
        customer.createdBy( createCustomerDto.getCreatedBy() );
        customer.modifiedBy( createCustomerDto.getModifiedBy() );

        return customer.build();
    }

    @Override
    public Customer toEntity(UpdateCustomerDto updateCustomerDto) {
        if ( updateCustomerDto == null ) {
            return null;
        }

        Customer.CustomerBuilder<?, ?> customer = Customer.builder();

        customer.customerId( updateCustomerDto.getCustomerId() );
        customer.phone( updateCustomerDto.getPhone() );
        customer.modifiedBy( updateCustomerDto.getModifiedBy() );

        return customer.build();
    }

    @Override
    public CreateCustomerDto.Response toCreateResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        Long customerId = null;
        String name = null;
        String email = null;
        String phone = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;
        Long createdBy = null;
        Long modifiedBy = null;

        customerId = customer.getCustomerId();
        name = customer.getName();
        email = customer.getEmail();
        phone = customer.getPhone();
        createdAt = customer.getCreatedAt();
        modifiedAt = customer.getModifiedAt();
        createdBy = customer.getCreatedBy();
        modifiedBy = customer.getModifiedBy();

        CreateCustomerDto.Response response = new CreateCustomerDto.Response( customerId, name, email, phone, createdAt, modifiedAt, createdBy, modifiedBy );

        return response;
    }

    @Override
    public UpdateCustomerDto.Response toUpdateResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        Long customerId = null;
        String name = null;
        String email = null;
        String phone = null;
        LocalDateTime createdAt = null;
        LocalDateTime modifiedAt = null;
        Long createdBy = null;
        Long modifiedBy = null;

        customerId = customer.getCustomerId();
        name = customer.getName();
        email = customer.getEmail();
        phone = customer.getPhone();
        createdAt = customer.getCreatedAt();
        modifiedAt = customer.getModifiedAt();
        createdBy = customer.getCreatedBy();
        modifiedBy = customer.getModifiedBy();

        UpdateCustomerDto.Response response = new UpdateCustomerDto.Response( customerId, name, email, phone, createdAt, modifiedAt, createdBy, modifiedBy );

        return response;
    }
}
