package com.nailcase.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.response.ResponseService;
import com.nailcase.response.SingleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CustomerRepository customerRepository;

	@MockBean
	private ResponseService responseService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void createCustomer_ValidInput_ReturnsCreatedCustomer() throws Exception {
		LocalDateTime now = LocalDateTime.now();

		Customer customer = Customer.builder()
			.name("jangdm")
			.email("jangdm@example.com")
			.phone("1234567890")
			.createdBy(1L)
			.modifiedBy(1L)
			.build();

		Customer savedCustomer = Customer.builder()
			.customerId(1L)
			.name("jangdm")
			.email("jangdm@example.com")
			.phone("1234567890")
			.createdAt(now)
			.modifiedAt(now)
			.createdBy(1L)
			.modifiedBy(1L)
			.build();

		when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
		SingleResponse<Customer> response = new SingleResponse<>();
		response.setData(savedCustomer);
		when(responseService.getSingleResponse(savedCustomer)).thenReturn(response);

		String requestBody = objectMapper.writeValueAsString(customer);

		mockMvc.perform(post("/customers/new")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.customerId").value(1))
			.andExpect(jsonPath("$.data.name").value("jangdm"))
			.andExpect(jsonPath("$.data.email").value("jangdm@example.com"))
			.andExpect(jsonPath("$.data.phone").value("1234567890"))
			.andExpect(jsonPath("$.data.createdBy").value(1))
			.andExpect(jsonPath("$.data.modifiedBy").value(1));
	}
}