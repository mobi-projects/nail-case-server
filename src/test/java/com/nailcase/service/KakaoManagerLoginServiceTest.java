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
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.OAuthAttributes;
import com.nailcase.oauth.service.KakaoManagerLoginService;
import com.nailcase.oauth.userInfo.OAuth2UserInfo;
import com.nailcase.repository.NailArtistRepository;

class KakaoManagerLoginServiceTest {

	@InjectMocks
	private KakaoManagerLoginService kakaoManagerLoginService;

	@Mock
	private NailArtistRepository nailArtistRepository;

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

		NailArtist nailArtist = NailArtist
			.builder()
			.email("test@example.com")
			.nailArtistId(1L)
			.role(Role.MANAGER).build();

		when(nailArtistRepository.findBySocialTypeAndSocialIdWithShops(any(), anyString()))
			.thenReturn(java.util.Optional.of(nailArtist));
		when(jwtService.createAccessToken(anyString(), anyLong(), any())).thenReturn("accessToken");
		when(jwtService.createRefreshToken(anyString(), anyLong(), any())).thenReturn("refreshToken");

		// Reflection을 사용하여 protected 메소드 호출
		Method processUserLoginMethod = KakaoManagerLoginService.class.getDeclaredMethod("processUserLogin",
			OAuthAttributes.class);
		processUserLoginMethod.setAccessible(true);
		LoginResponseDto response = (LoginResponseDto)processUserLoginMethod.invoke(kakaoManagerLoginService,
			attributes);

		assertNotNull(response);
		assertEquals("accessToken", response.getAccessToken());
		assertEquals("refreshToken", response.getRefreshToken());
		assertEquals(Role.MANAGER, response.getRole());
		verify(jwtService).updateRefreshToken(anyString(), anyString(), any());
	}
}