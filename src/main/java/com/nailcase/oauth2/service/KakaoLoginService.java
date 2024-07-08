package com.nailcase.oauth2.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.SocialType;
import com.nailcase.oauth2.dto.OAuthAttributes;
import com.nailcase.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service("kakaoLoginService")
@RequiredArgsConstructor
public class KakaoLoginService implements SocialLoginService {

	private final MemberRepository memberRepository;
	private final RestTemplate restTemplate;
	private final JwtService jwtService;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	@Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
	private String kakaoUserInfoUri;

	@Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
	private String authorizationCode;

	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String tokenUri;

	private final String GRANT_TYPE = "grant_type";
	private final String CLIENT_ID = "client_id";
	private final String REDIRECT_URI = "redirect_uri";
	private final String CODE_NUMBER = "code";

	@Value("${jwt.access.name}")
	private String accessToken;

	@Override
	public Map<String, String> processLogin(String code) {
		String accessToken = getKakaoAccessToken(code);
		Map<String, Object> userAttributes = getKakaoUserAttributes(accessToken);
		OAuthAttributes attributes = OAuthAttributes.of("kakao", "id", userAttributes);
		Member member = getOrCreateMember(attributes, SocialType.KAKAO);
		String accessTokenJwt = jwtService.createAccessToken(member.getEmail(), member.getMemberId());
		String refreshToken = jwtService.createRefreshToken(member.getEmail());
		jwtService.updateRefreshToken(member.getEmail(), refreshToken);

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessTokenJwt);
		tokens.put("refreshToken", refreshToken);
		return tokens;
	}

	public Member getOrCreateMember(OAuthAttributes attributes, SocialType socialType) {
		return memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
			.orElseGet(() -> memberRepository.save(attributes.toEntity(socialType, attributes.getOauth2UserInfo())));
	}

	private String getKakaoAccessToken(String code) {
		String url = tokenUri;
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(GRANT_TYPE, authorizationCode);
		params.add(CLIENT_ID, kakaoClientId);
		params.add(REDIRECT_URI, kakaoRedirectUri);
		params.add(CODE_NUMBER, code);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			Map<String, Object> responseBody = response.getBody();
			return responseBody.get(accessToken).toString();
		} else {
			throw new RuntimeException("Failed to retrieve access token");
		}
	}

	private Map<String, Object> getKakaoUserAttributes(String accessToken) {
		String url = kakaoUserInfoUri;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);

		HttpEntity<String> request = new HttpEntity<>(headers);
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();  // Map<String, Object>를 직접 반환
		} else {
			throw new BusinessException(AuthErrorCode.AUTH_UNEXPECTED,
				"Failed to retrieve user information from Kakao");
		}
	}

}