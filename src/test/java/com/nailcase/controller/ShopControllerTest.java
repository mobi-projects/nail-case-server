// package com.nailcase.controller;
//
// import static org.jeasy.random.FieldPredicates.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import org.jeasy.random.EasyRandom;
// import org.jeasy.random.EasyRandomParameters;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.context.WebApplicationContext;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import com.nailcase.config.SecurityConfig;
// import com.nailcase.model.dto.ShopDto;
// import com.nailcase.service.ShopService;
// import com.nailcase.testUtils.FixtureFactory;
// import com.nailcase.testUtils.Reflection;
// import com.nailcase.testUtils.StringGenerateFixture;
//
// @Transactional
// @SpringBootTest
// public class ShopControllerTest {
//
// 	@MockBean
// 	private ShopService shopService;
//
// 	@Autowired
// 	private FixtureFactory fixtureFactory;
//
// 	@Autowired
// 	private WebApplicationContext context;
//
// 	@Autowired
// 	private SecurityConfig securityConfig;
//
// 	private MockMvc mockMvc;
//
// 	private ObjectMapper om;x2
//
// 	@BeforeEach
// 	public void init() {
// 		mockMvc = MockMvcBuilders
// 			.webAppContextSetup(context)
// 			.addFilter(securityConfig.jwtAuthenticationProcessingFilter())
// 			.build();
// 		om = new ObjectMapper();
// 	}
//
// 	@Test
// 	@DisplayName("Post요청시 HTTP.CREATE와 응답을 반환한다.")
// 	void registerShopSuccess() throws Exception {
// 		// Given
// 		String jwt = fixtureFactory.getNailArtistFixtureToBootTest().createNailArtistAndGetJwt();
// 		EasyRandomParameters params = new EasyRandomParameters()
// 			.randomize(named("phone"), () -> StringGenerateFixture.makeByNumbersAndAlphabets(16));
// 		ShopDto.Post requestDto = new EasyRandom(params).nextObject(ShopDto.Post.class);
// 		ShopDto.Response responseDto = new ShopDto.Response();
// 		Reflection.setField(responseDto, "shopId", 1L);
// 		String requestJson = om.registerModule(new JavaTimeModule())
// 			.writeValueAsString(requestDto);
//
// 		// When
// 		doReturn(responseDto).when(shopService).registerShop(any(ShopDto.Post.class), eq(1L));
//
// 		// Then
// 		mockMvc
// 			.perform(post("/shops")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(requestJson)
// 				.header("Authorization", "Bearer " + jwt))
// 			.andExpect(status().isCreated());
// 	}
//
// 	@Test
// 	@DisplayName("Post요청시 필수 필드 누락으로 인해 HTTP.BAD_REQUEST를 반환한다.")
// 	void registerShopMissingRequiredFields() throws Exception {
// 		// Given
// 		String jwt = fixtureFactory.getNailArtistFixtureToBootTest().createNailArtistAndGetJwt();
//
// 		// 필수 필드(shopName, phone)가 누락된 요청 데이터
// 		EasyRandomParameters params = new EasyRandomParameters()
// 			.excludeField(named("shopName").and(ofType(String.class)))
// 			.excludeField(named("phone").and(ofType(String.class)));
// 		ShopDto.Post requestDto = new EasyRandom(params).nextObject(ShopDto.Post.class);
//
// 		String requestJson = om.registerModule(new JavaTimeModule())
// 			.writeValueAsString(requestDto);
//
// 		// When & Then
// 		mockMvc
// 			.perform(post("/shops")
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(requestJson)
// 				.header("Authorization", "Bearer " + jwt))
// 			.andExpect(status().isBadRequest());
// 	}
// }
