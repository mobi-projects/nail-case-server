package com.nailcase.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.dto.request.CreateCustomerRequest;
import com.nailcase.customer.dto.request.UpdateCustomerRequest;
import com.nailcase.customer.dto.response.CreateCustomerResponse;
import com.nailcase.customer.dto.response.UpdateCustomerResponse;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.exception.BusinessException;
import com.nailcase.response.CommonResponse;
import com.nailcase.response.ListResponse;
import com.nailcase.response.ResponseService;
import com.nailcase.response.SingleResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
	private Customer existingCustomer;
	private List<Customer> customers;


	@BeforeEach
	public void setup() {
		existingCustomer = Customer.builder()
			.customerId(1L)
			.name("jangdm")
			.email("jangdm@example.com")
			.phone("1234567890")
			.createdBy(1L)
			.modifiedBy(1L)
			.build();

		Customer secondCustomer = Customer.builder()
			.customerId(2L)
			.name("woody")
			.email("woody@example.com")
			.phone("9876543210")
			.createdBy(1L)
			.modifiedBy(1L)
			.build();

		customers = Arrays.asList(existingCustomer, secondCustomer);

		when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(existingCustomer));
		when(customerRepository.findAll()).thenReturn(customers);
	}



	@DisplayName("customer 조회 테스트")
	@Test
	public void getCustomerTest() throws Exception {
		SingleResponse<Customer> response = new SingleResponse<>();
		response.setData(existingCustomer);
		when(responseService.getSingleResponse(eq(existingCustomer))).thenReturn(response);

		mockMvc.perform(get("/api/v1/customers/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.customerId").value(existingCustomer.getCustomerId()))
			.andExpect(jsonPath("$.data.name").value(existingCustomer.getName()))
			.andExpect(jsonPath("$.data.email").value(existingCustomer.getEmail()));
	}


	@DisplayName("모든 customers 조회 테스트")
	@Test
	public void getAllCustomersTest() throws Exception {
		ListResponse<Customer> listResponse = new ListResponse<>();
		listResponse.setDataList(customers);
		listResponse.setSuccess(true);
		listResponse.setCode(CommonResponse.SUCCESS_CODE);
		listResponse.setMessage(CommonResponse.SUCCESS_MESSAGE);

		when(responseService.getListResponse(customers)).thenReturn(listResponse);

		mockMvc.perform(get("/api/v1/customers/all")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.dataList[0].customerId").value(customers.get(0).getCustomerId()))
			.andExpect(jsonPath("$.dataList[0].name").value("jangdm"))
			.andExpect(jsonPath("$.dataList[0].email").value("jangdm@example.com"))
			.andExpect(jsonPath("$.dataList[1].customerId").value(customers.get(1).getCustomerId()))
			.andExpect(jsonPath("$.dataList[1].name").value("woody"))
			.andExpect(jsonPath("$.dataList[1].email").value("woody@example.com"));
	}



	@DisplayName("customer 생성 테스트")
	@Test
	public void createCustomerTest() throws Exception {
		LocalDateTime now = LocalDateTime.now();

		CreateCustomerRequest request = new CreateCustomerRequest(
			"jangdm",
			"jangdm@example.com",
			"1234567890",
			1L,
			1L
		);

		Customer savedCustomer = Customer.builder()
			.customerId(1L)
			.name(request.getName())
			.email(request.getEmail())
			.phone(request.getPhone())
			.createdAt(now)
			.modifiedAt(now)
			.createdBy(request.getCreatedBy())
			.modifiedBy(request.getModifiedBy())
			.build();

		CreateCustomerResponse response = new CreateCustomerResponse(
			savedCustomer.getCustomerId(),
			savedCustomer.getName(),
			savedCustomer.getEmail(),
			savedCustomer.getPhone(),
			savedCustomer.getCreatedAt(),
			savedCustomer.getModifiedAt(),
			savedCustomer.getCreatedBy(),
			savedCustomer.getModifiedBy()
		);

		SingleResponse<CreateCustomerResponse> singleResponse = new SingleResponse<>();
		singleResponse.setData(response);

		when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
		when(responseService.getSingleResponse(response)).thenReturn(singleResponse);

		String requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/api/v1/customers/signup")
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


	@DisplayName("customer 수정 테스트")
	@Test
	public void updateCustomerTest() throws Exception {
		UpdateCustomerRequest updateRequest = new UpdateCustomerRequest();
		updateRequest.setCustomerId(existingCustomer.getCustomerId());
		updateRequest.setPhone("0987654321");
		updateRequest.setModifiedBy(2L);

		Customer updatedCustomer = Customer.builder()
			.customerId(existingCustomer.getCustomerId())
			.name(existingCustomer.getName())
			.email(existingCustomer.getEmail())
			.phone(updateRequest.getPhone())
			.createdBy(existingCustomer.getCreatedBy())
			.modifiedBy(updateRequest.getModifiedBy())
			.createdAt(existingCustomer.getCreatedAt())
			.modifiedAt(LocalDateTime.now())
			.build();

		when(customerRepository.findById(existingCustomer.getCustomerId())).thenReturn(java.util.Optional.of(existingCustomer));
		when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

		UpdateCustomerResponse updateResponse = new UpdateCustomerResponse(
			updatedCustomer.getCustomerId(),
			updatedCustomer.getName(),
			updatedCustomer.getEmail(),
			updatedCustomer.getPhone(),
			updatedCustomer.getModifiedBy(),
			updatedCustomer.getModifiedAt()
		);

		SingleResponse<UpdateCustomerResponse> response = new SingleResponse<>();
		response.setData(updateResponse);
		when(responseService.getSingleResponse(updateResponse)).thenReturn(response);

		mockMvc.perform(put("/api/v1/customers/edit/" + existingCustomer.getCustomerId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.phone").value("0987654321"))
			.andExpect(jsonPath("$.data.modifiedBy").value(2L));
	}



	@DisplayName("회원탈퇴 로직")
	@Test
	public void deleteCustomerTest() throws Exception {
		when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));

		mockMvc.perform(delete("/api/v1/customers/delete/{id}", 1L))
			.andExpect(status().isOk());

		verify(customerRepository, times(1)).deleteById(1L);

		when(customerRepository.findById(1L)).thenReturn(Optional.empty());

		Optional<Customer> deletedCustomer = customerRepository.findById(1L);
		assertTrue(deletedCustomer.isEmpty());
	}

}