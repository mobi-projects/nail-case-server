package com.nailcase.oauth.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.OAuthAttributes;

public abstract class AbstractKakaoLoginService implements SocialLoginService {

	protected final RestTemplate restTemplate;
	protected final JwtService jwtService;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	protected String kakaoClientId;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	protected String kakaoRedirectUri;

	@Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
	protected String kakaoUserInfoUri;

	@Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
	protected String authorizationCode;

	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	protected String tokenUri;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	protected String kakaoClientSecret;

	@Value("${jwt.access.name}")
	protected String accessTokenName;

	protected final String GRANT_TYPE = "grant_type";
	protected final String CLIENT_ID = "client_id";
	protected final String REDIRECT_URI = "redirect_uri";
	protected final String CODE_NUMBER = "code";
	protected final String CLIENT_SECRET = "client_secret";

	public AbstractKakaoLoginService(RestTemplate restTemplate, JwtService jwtService) {
		this.restTemplate = restTemplate;
		this.jwtService = jwtService;
	}

	@Override
	public LoginResponseDto processLogin(String code) {
		String accessToken = getKakaoAccessToken(code);
		Map<String, Object> userAttributes = getKakaoUserAttributes(accessToken);
		OAuthAttributes attributes = OAuthAttributes.of("kakao", "id", userAttributes);
		return processUserLogin(attributes);
	}

	protected abstract LoginResponseDto processUserLogin(OAuthAttributes attributes);

	protected String getKakaoAccessToken(String code) {
		String url = tokenUri;
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(GRANT_TYPE, authorizationCode);
		params.add(CLIENT_ID, kakaoClientId);
		params.add(REDIRECT_URI, kakaoRedirectUri);
		params.add(CODE_NUMBER, code);
		params.add(CLIENT_SECRET, kakaoClientSecret);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			Map<String, Object> responseBody = response.getBody();
			return responseBody.get(accessTokenName).toString();
		} else {
			throw new BusinessException(AuthErrorCode.ACCESS_RETRIEVE);
		}
	}

	protected Map<String, Object> getKakaoUserAttributes(String accessToken) {
		String url = kakaoUserInfoUri;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		} else {
			throw new BusinessException(AuthErrorCode.ACCESS_RETRIEVE);
		}
	}
}
