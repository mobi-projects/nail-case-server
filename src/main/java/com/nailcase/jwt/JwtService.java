package com.nailcase.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtService {

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.access.expiration}")
	private Long accessTokenExpirationPeriod;

	@Value("${jwt.refresh.expiration}")
	private Long refreshTokenExpirationPeriod;

	@Value("${jwt.access.header}")
	private String accessHeader;

	@Value("${jwt.refresh.header}")
	private String refreshHeader;

	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String EMAIL_CLAIM = "email";
	private static final String BEARER = "Bearer ";

	private final CustomerRepository customerRepository;
	private final RedisTemplate<String, String> redisTemplate;

	public String createAccessToken(String email) {

		Date now = new Date();
		return JWT.create()
			.withSubject(ACCESS_TOKEN_SUBJECT)
			.withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
			.withClaim(EMAIL_CLAIM, email)
			.sign(Algorithm.HMAC512(secretKey));
	}

	public String createRefreshToken() {
		Date now = new Date();
		return JWT.create()
			.withSubject(REFRESH_TOKEN_SUBJECT)
			.withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
			.sign(Algorithm.HMAC512(secretKey));
	}

	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader(accessHeader, accessToken);
		log.info("재발급된 Access Token : {}", accessToken);
	}

	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		setAccessTokenHeader(response, accessToken);
		setRefreshTokenHeader(response, refreshToken);
		log.info("Access Token, Refresh Token 헤더 설정 완료");
	}

	public Optional<String> extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(refreshHeader))
			.filter(refreshToken -> refreshToken.startsWith(BEARER))
			.map(refreshToken -> refreshToken.replace(BEARER, ""));
	}

	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(accessHeader))
			.filter(accessToken -> accessToken.startsWith(BEARER))
			.map(accessToken -> accessToken.replace(BEARER, ""));
	}

	public Optional<String> extractEmail(String accessToken) {
		try {
			return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
				.build()
				.verify(accessToken)
				.getClaim(EMAIL_CLAIM)
				.asString());
		} catch (Exception e) {
			log.error("액세스 토큰이 유효하지 않습니다.");
			return Optional.empty();
		}
	}

	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(accessHeader, accessToken);
	}

	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(refreshHeader, refreshToken);
	}

	public void updateRefreshToken(String email, String refreshToken) {
		customerRepository.findByEmail(email).ifPresentOrElse(
			user -> {
				redisTemplate.opsForValue()
					.set(email, refreshToken, refreshTokenExpirationPeriod, TimeUnit.MILLISECONDS);
			},
			() -> {
				throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
			}
		);
	}

	public boolean isTokenValid(String token) {
		try {
			JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
			return true;
		} catch (Exception e) {
			log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
			return false;
		}
	}
}
