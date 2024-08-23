// package com.nailcase.controller;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.util.List;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.ResponseEntity;
//
// import com.nailcase.exception.BusinessException;
// import com.nailcase.model.enums.Role;
// import com.nailcase.oauth.controller.AuthController;
// import com.nailcase.oauth.dto.LoginResponseDto;
// import com.nailcase.oauth.service.AbstractKakaoLoginService;
//
// @SpringBootTest
// class AuthControllerTest {
//
// 	@InjectMocks
// 	private AuthController authController;
//
// 	@Mock
// 	private AbstractKakaoLoginService kakaoMemberLoginService;
//
// 	@Mock
// 	private AbstractKakaoLoginService kakaoManagerLoginService;
//
// 	@BeforeEach
// 	void setUp() {
// 		MockitoAnnotations.openMocks(this);
// 		authController = new AuthController(kakaoMemberLoginService, kakaoManagerLoginService);
// 	}
//
// 	@Test
// 	@DisplayName("로그아웃 요청 테스트")
// 	void testLogout() {
// 		ResponseEntity<?> response = authController.logout();
// 		assertNotNull(response);
// 		assertEquals(200, response.getStatusCodeValue());
// 	}
//
// 	@Test
// 	@DisplayName("회원 로그인 요청 테스트")
// 	void testSocialLoginForMember() {
// 		String code = "testCode";
// 		LoginResponseDto expectedResponse = LoginResponseDto.builder()
// 			.accessToken("testAccessToken")
// 			.refreshToken("testRefreshToken")
// 			.shopIds(List.of(1L, 2L))
// 			.hasShop(true)
// 			.role(Role.MEMBER)
// 			.profileImgUrl("http://example.com/profile.jpg")
// 			.build();
// 		when(kakaoMemberLoginService.processLogin(anyString())).thenReturn(expectedResponse);
//
// 		LoginResponseDto actualResponse = authController.socialLogin("member", "kakao", code);
//
// 		assertNotNull(actualResponse, "null값이 될 수 없다.");
// 		assertEquals(expectedResponse, actualResponse);
// 		verify(kakaoMemberLoginService).processLogin(code);
// 	}
//
// 	@Test
// 	@DisplayName("매니저 로그인 요청 테스트")
// 	void testSocialLoginForManager() {
// 		String code = "testCode";
// 		LoginResponseDto expectedResponse = new LoginResponseDto();
// 		when(kakaoManagerLoginService.processLogin(code)).thenReturn(expectedResponse);
//
// 		LoginResponseDto actualResponse = authController.socialLogin("manager", "kakao", code);
//
// 		assertEquals(expectedResponse, actualResponse);
// 		verify(kakaoManagerLoginService).processLogin(code);
// 	}
//
// 	@Test
// 	@DisplayName("지원하지 않는 서비스로 로그인 요청 시 BusinessException 발생")
// 	void testSocialLoginWithUnsupportedService() {
// 		assertThrows(BusinessException.class, () ->
// 			authController.socialLogin("member", "unsupported", "testCode"));
// 	}
// }
