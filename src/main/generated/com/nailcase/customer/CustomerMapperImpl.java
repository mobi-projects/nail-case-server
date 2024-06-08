package com.nailcase.customer;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CustomerDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-08T18:43:20+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toEntity(CustomerDto.Request customerDto) {
        if ( customerDto == null ) {
            return null;
        }

        Customer.CustomerBuilder<?, ?> customer = Customer.builder();

        customer.name( customerDto.getName() );
        customer.email( customerDto.getEmail() );
        customer.phone( customerDto.getPhone() );

        return customer.build();
    }

    @Override
    public CustomerDto.Response toResponse(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        Long customerId = null;
        String name = null;
        String email = null;
        String phone = null;
        Long createdBy = null;
        Long modifiedBy = null;

        customerId = customer.getCustomerId();
        name = customer.getName();
        email = customer.getEmail();
        phone = customer.getPhone();
        if ( customer.getCreatedBy() != null ) {
            createdBy = Long.parseLong( customer.getCreatedBy() );
        }
        if ( customer.getModifiedBy() != null ) {
            modifiedBy = Long.parseLong( customer.getModifiedBy() );
        }

        Long createdAt = null;
        Long modifiedAt = null;

        CustomerDto.Response response = new CustomerDto.Response( customerId, name, email, phone, createdAt, modifiedAt, createdBy, modifiedBy );

        beforeMapping( customer, response );

        return response;
    }
}
