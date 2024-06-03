package com.nailcase.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.customer.CustomerMapper;
import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.dto.CreateCustomerDto;
import com.nailcase.customer.domain.dto.UpdateCustomerDto;
import com.nailcase.customer.service.CustomerService;
import com.nailcase.response.ListResponse;
import com.nailcase.response.ResponseService;
import com.nailcase.response.SingleResponse;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.testUtils.StringGenerateFixture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CustomerControllerTest {


	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper;

	@MockBean
	private CustomerService customerService;

	@MockBean
	private ResponseService responseService;

	private Customer existingCustomer;

	public CustomerControllerTest(MockMvc mockMvc,
		ObjectMapper objectMapper,
		CustomerService customerService,
		ResponseService responseService) {
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
		this.customerService = customerService;
		this.responseService = responseService;
	}


	@BeforeEach
	public void setup() {
		existingCustomer = Customer.builder()
			.customerId(1L)
			.name(StringGenerateFixture.makeByNumbersAndAlphabets(10))
			.email(StringGenerateFixture.makeEmail(20))
			.phone(StringGenerateFixture.makeByNumbersAndLowerLetters(11))
			.createdBy(1L)
			.modifiedBy(1L)
			.build();
	}


	@DisplayName("customer 생성 테스트")
	@Test
	public void createCustomerTest() throws Exception {
		LocalDateTime now = LocalDateTime.now();

		CreateCustomerDto request = new CreateCustomerDto(
			StringGenerateFixture.makeByNumbersAndAlphabets(10),
			StringGenerateFixture.makeEmail(20),
			StringGenerateFixture.makeByNumbersAndLowerLetters(11),
			1L,
			1L
		);

		Customer savedCustomer = Customer.builder()
			.customerId(1L)
			.name(request.getName())
			.email(request.getEmail())
			.phone(request.getPhone())
			.createdBy(request.getCreatedBy())
			.modifiedBy(request.getModifiedBy())
			.build();

		CreateCustomerDto.Response response = CustomerMapper.INSTANCE.toCreateResponse(savedCustomer);

		SingleResponse<CreateCustomerDto.Response> singleResponse = new SingleResponse<>();
		singleResponse.setData(response);

		when(customerService.createCustomer(any(CreateCustomerDto.class))).thenReturn(response);
		when(responseService.getSingleResponse(response)).thenReturn(singleResponse);

		String requestBody = objectMapper.writeValueAsString(request);

		mockMvc.perform(post("/customers/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.data.customerId").value(1))
			.andExpect(jsonPath("$.data.name").isString())
			.andExpect(jsonPath("$.data.email").isString())
			.andExpect(jsonPath("$.data.phone").isString())
			.andExpect(jsonPath("$.data.createdBy").value(1))
			.andExpect(jsonPath("$.data.modifiedBy").value(1));
	}



	@DisplayName("customer 수정 테스트")
	@Test
	public void updateCustomerTest() throws Exception {
		UpdateCustomerDto updateRequest = new UpdateCustomerDto();
		updateRequest.setCustomerId(existingCustomer.getCustomerId());
		updateRequest.setPhone(StringGenerateFixture.makeByNumbersAndLowerLetters(11));
		updateRequest.setModifiedBy(2L);

		Customer updatedCustomer = Customer.builder()
			.customerId(existingCustomer.getCustomerId())
			.name(existingCustomer.getName())
			.email(existingCustomer.getEmail())
			.phone(updateRequest.getPhone())
			.createdBy(existingCustomer.getCreatedBy())
			.modifiedBy(updateRequest.getModifiedBy())
			.build();

		UpdateCustomerDto.Response updateResponse = new UpdateCustomerDto.Response(
			updatedCustomer.getCustomerId(),
			updatedCustomer.getName(),
			updatedCustomer.getEmail(),
			updatedCustomer.getPhone(),
			updatedCustomer.getCreatedAt(),
			updatedCustomer.getModifiedAt(),
			updatedCustomer.getCreatedBy(),
			updatedCustomer.getModifiedBy()
		);

		SingleResponse<UpdateCustomerDto.Response> response = new SingleResponse<>();
		response.setData(updateResponse);

		when(customerService.updateCustomer(eq(existingCustomer.getCustomerId()), any(UpdateCustomerDto.class))).thenReturn(updateResponse);
		when(responseService.getSingleResponse(updateResponse)).thenReturn(response);

		mockMvc.perform(put("/customers/{id}", existingCustomer.getCustomerId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.phone").isString())
			.andExpect(jsonPath("$.data.modifiedBy").value(2L));
	}

	@DisplayName("모든 customers 조회 테스트")
	@Test
	public void getAllCustomersTest() throws Exception {
		Customer customer1 = Customer.builder()
			.customerId(1L)
			.name(StringGenerateFixture.makeByNumbersAndAlphabets(8))
			.email(StringGenerateFixture.makeEmail(15))
			.phone(StringGenerateFixture.makeByNumbersAndLowerLetters(11))
			.createdBy(1L)
			.modifiedBy(1L)
			.build();

		Customer customer2 = Customer.builder()
			.customerId(2L)
			.name(StringGenerateFixture.makeByNumbersAndAlphabets(8))
			.email(StringGenerateFixture.makeEmail(15))
			.phone(StringGenerateFixture.makeByNumbersAndLowerLetters(11))
			.createdBy(1L)
			.modifiedBy(1L)
			.build();

		List<Customer> customers = List.of(customer1, customer2);

		ListResponse<Customer> listResponse = new ListResponse<>();
		listResponse.setDataList(customers);

		when(customerService.getAllCustomers()).thenReturn(customers);
		when(responseService.getListResponse(customers)).thenReturn(listResponse);

		mockMvc.perform(get("/customers/all")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.dataList[0].customerId").value(customer1.getCustomerId()))
			.andExpect(jsonPath("$.dataList[0].name").isString())
			.andExpect(jsonPath("$.dataList[0].email").isString())
			.andExpect(jsonPath("$.dataList[0].email").value(customer1.getEmail()))
			.andExpect(jsonPath("$.dataList[0].phone").isString())
			.andExpect(jsonPath("$.dataList[0].phone").value(customer1.getPhone()))
			.andExpect(jsonPath("$.dataList[1].customerId").value(customer2.getCustomerId()))
			.andExpect(jsonPath("$.dataList[1].name").isString())
			.andExpect(jsonPath("$.dataList[1].email").isString())
			.andExpect(jsonPath("$.dataList[1].email").value(customer2.getEmail()))
			.andExpect(jsonPath("$.dataList[1].phone").isString())
			.andExpect(jsonPath("$.dataList[1].phone").value(customer2.getPhone()));
	}

	@DisplayName("회원탈퇴 로직")
	@Test
	public void deleteCustomerTest() throws Exception {
		when(customerService.getCustomerById(1L)).thenReturn(existingCustomer);

		mockMvc.perform(delete("/customers/delete/{id}", 1L))
			.andExpect(status().isOk());

		verify(customerService, times(1)).deleteCustomer(1L);

		doThrow(new BusinessException(UserErrorCode.USER_NOT_FOUND)).when(customerService).getCustomerById(1L);

		assertThrows(BusinessException.class, () -> {
			customerService.getCustomerById(1L);
		});
	}
}
