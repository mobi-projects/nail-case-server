package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.Role;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.OAuthAttributes;
import com.nailcase.oauth.service.KakaoMemberLoginService;
import com.nailcase.oauth.userInfo.OAuth2UserInfo;
import com.nailcase.repository.MemberRepository;

class KakaoMemberLoginServiceTest {

	@InjectMocks
	private KakaoMemberLoginService kakaoMemberLoginService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testProcessUserLogin() throws Exception {
		// OAuth2UserInfo mock 생성
		OAuth2UserInfo oAuth2UserInfo = mock(OAuth2UserInfo.class);
		when(oAuth2UserInfo.getId()).thenReturn("testId");

		// OAuthAttributes mock 생성 및 설정
		OAuthAttributes attributes = mock(OAuthAttributes.class);
		when(attributes.getOauth2UserInfo()).thenReturn(oAuth2UserInfo);

		Member member = Member.builder()
			.email("test@example.com")
			.memberId(1L)
			.role(Role.MEMBER)
			.build();

		when(memberRepository.findBySocialTypeAndSocialId(any(), anyString()))
			.thenReturn(java.util.Optional.of(member));
		when(jwtService.createAccessToken(anyString(), anyLong(), any())).thenReturn("accessToken");
		when(jwtService.createRefreshToken(anyString(), anyLong(), any())).thenReturn("refreshToken");

		// Reflection을 사용하여 protected 메소드 호출
		Method processUserLoginMethod = KakaoMemberLoginService.class.getDeclaredMethod("processUserLogin",
			OAuthAttributes.class);
		processUserLoginMethod.setAccessible(true);
		LoginResponseDto response = (LoginResponseDto)processUserLoginMethod.invoke(kakaoMemberLoginService,
			attributes);

		assertNotNull(response);
		assertEquals("accessToken", response.getAccessToken());
		assertEquals("refreshToken", response.getRefreshToken());
		assertEquals(Role.MEMBER, response.getRole());
		verify(jwtService).updateRefreshToken(anyString(), anyString(), any());
	}
}