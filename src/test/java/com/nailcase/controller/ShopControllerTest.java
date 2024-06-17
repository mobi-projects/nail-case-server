package com.nailcase.controller;

import static org.jeasy.random.FieldPredicates.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nailcase.config.SecurityConfig;
import com.nailcase.model.dto.ShopRegisterDto;
import com.nailcase.service.ShopService;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.Reflection;
import com.nailcase.testUtils.StringGenerateFixture;

@Transactional
@SpringBootTest
public class ShopControllerTest {

	@Mock
	private ShopService shopService;

	@Autowired
	private FixtureFactory fixtureFactory;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private SecurityConfig securityConfig;

	private MockMvc mockMvc;

	private ObjectMapper om;

	@BeforeEach
	public void init() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.addFilter(securityConfig.jwtAuthenticationProcessingFilter())
			.build();
		om = new ObjectMapper();
	}

	@Test
	@DisplayName("Post요청시 HTTP.CREATE와 응답을 반환한다.")
	void registerShopSuccess() throws Exception {
		// Given
		String jwt = fixtureFactory.getMemberFixtureFactory().createMemberAndGetJwt();
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("phone"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(16));
		ShopRegisterDto.Request requestDto = new EasyRandom(params).nextObject(ShopRegisterDto.Request.class);
		ShopRegisterDto.Response responseDto = new ShopRegisterDto.Response();
		Reflection.setField(responseDto, "shopId", 1L);
		String requestJson = om.registerModule(new JavaTimeModule())
			.writeValueAsString(requestDto);
		String responseJson = om.registerModule(new JavaTimeModule())
			.writeValueAsString(responseDto);

		// When
		doReturn(responseDto).when(shopService).registerShop(any(ShopRegisterDto.Request.class), eq(1L));

		mockMvc
			.perform(post("/shops")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
				.header("Authorization", "Bearer " + jwt))
			.andExpect(status().isCreated())
			.andExpect(content().json(responseJson));
	}
}