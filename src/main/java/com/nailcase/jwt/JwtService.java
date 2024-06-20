package com.nailcase.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.repository.MemberRepository;

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
	private static final String MEMBER_CLAIM = "memberId";
	private static final String BEARER = "Bearer ";

	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	public String createAccessToken(String email, Long memberId) {
		Date now = new Date();
		String token = JWT.create()
			.withSubject(ACCESS_TOKEN_SUBJECT)
			.withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
			.withClaim(EMAIL_CLAIM, email)
			.withClaim(MEMBER_CLAIM, memberId)
			.sign(Algorithm.HMAC512(secretKey));
		log.info("{} 해당 유저에 대한 AccessToken 발급", email);
		return token;
	}

	public String createRefreshToken(String email) {
		Date now = new Date();
		String token = JWT.create()
			.withSubject(REFRESH_TOKEN_SUBJECT)
			.withClaim(EMAIL_CLAIM, email)
			.withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
			.sign(Algorithm.HMAC512(secretKey));
		log.info("{} 해당 유저에 대한 RefreshToken 발급", email);
		return token;
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
			log.error("유효하지 않은 토큰입니다 : {}", accessToken, e);
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
		memberRepository.findByEmail(email).ifPresentOrElse(
			user -> {
				redisTemplate.opsForValue()
					.set(email, refreshToken, refreshTokenExpirationPeriod, TimeUnit.MILLISECONDS);
				log.info("레디스에 refreshToken이 업데이트 되었습니다. {}", email);
			},
			() -> {
				log.error("찾지 못한 유저 {}", email);
				throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
			}
		);
	}

	public boolean isTokenValid(String token) {
		try {
			JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
			// 블랙리스트에 있는지 확인
			Boolean isBlacklisted = (Boolean)redisTemplate.opsForValue().get("blacklist:" + token);
			return isBlacklisted == null || !isBlacklisted;
		} catch (Exception e) {
			log.error("유효하지 않은 토큰 : {}", token, e);
			return false;
		}
	}

	public void logoutUser(String email) {
		redisTemplate.delete(email);
		log.info("{} 유저의 세션이 종료되었습니다.", email);
	}

	public void addTokenToBlacklist(String token) {
		// 토큰의 남은 유효 시간 계산
		long remainingTime = JWT.decode(token).getExpiresAt().getTime() - System.currentTimeMillis();
		// 토큰을 블랙리스트에 추가 (남은 유효 시간 동안만 블랙리스트에 유지)
		redisTemplate.opsForValue().set("blacklist:" + token, true, remainingTime, TimeUnit.MILLISECONDS);
	}

}
