package com.nailcase.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.model.enums.UserType;
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
	private static final String ID_CLAIM = "sequenceId";
	private static final String USERTYPE_CLAIM = "userType";
	private static final String BEARER = "Bearer ";

	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	public String createAccessToken(String email, Long sequenceId, String userType) {
		Date now = new Date();
		String token = JWT.create()
			.withSubject(ACCESS_TOKEN_SUBJECT)
			.withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
			.withClaim(EMAIL_CLAIM, email)
			.withClaim(USERTYPE_CLAIM, userType)
			.withClaim(ID_CLAIM, sequenceId)
			.sign(Algorithm.HMAC512(secretKey));
		log.info("{} 해당 유저에 대한 AccessToken 발급", email);
		return token;
	}

	public String createRefreshToken(String email, String userType) {
		Date now = new Date();
		String token = JWT.create()
			.withSubject(REFRESH_TOKEN_SUBJECT)
			.withClaim(EMAIL_CLAIM, email)
			.withClaim(USERTYPE_CLAIM, userType)
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
			log.error("유효하지 않은 토큰으로 이메일 추출 시도: {}", accessToken, e);
			throw new BusinessException(AuthErrorCode.TOKEN_INVALID, "토큰이 유효하지 않습니다.");
		}
	}

	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(accessHeader, accessToken);
	}

	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(refreshHeader, refreshToken);
	}

	public void updateRefreshToken(String email, String refreshToken, String userType) {
		String key = userType + ":" + email;
		redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpirationPeriod, TimeUnit.MILLISECONDS);
	}

	public boolean isTokenValid(String token) {
		try {
			token = removeBearerInToken(token);
			DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
			String jti = decodedJWT.getId();
			Boolean isBlacklisted = (Boolean)redisTemplate.opsForValue().get("blacklist:" + jti);
			return isBlacklisted == null && decodedJWT.getExpiresAt().after(new Date());
		} catch (Exception e) {
			log.error("유효하지 않은 토큰 : {}", token, e);
			return false;
		}
	}

	public void addTokenToBlacklist(String token) {
		token = removeBearerInToken(token);
		DecodedJWT decoded = verifyToken(token);
		long remainingTime = decoded.getExpiresAt().getTime() - System.currentTimeMillis();
		redisTemplate.opsForValue().set("blacklist:" + decoded.getId(), true, remainingTime, TimeUnit.MILLISECONDS);
		log.info("토큰 블랙리스트에 추가: JWT ID {}", decoded.getId());
	}

	public void logoutAndBlacklistToken(String accessToken) {
		accessToken = removeBearerInToken(accessToken);
		if (!isTokenValid(accessToken)) {
			throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
		}

		String email = extractEmail(accessToken)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));

		UserType userType = extractUserType(accessToken)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));

		String redisKey = userType.getValue() + ":" + email;
		redisTemplate.delete(redisKey);

		addTokenToBlacklist(accessToken);
	}

	public void removeRefreshToken(String refreshToken) {
		String email = extractEmail(refreshToken)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
		UserType userType = extractUserType(refreshToken)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
		String key = userType.getValue() + ":" + email;
		Boolean deleted = redisTemplate.delete(key);
		if (Boolean.FALSE.equals(deleted)) {
			log.warn("해당 계정의 refresh 토큰이 레디스에 없습니다: {}", email);
		}
	}

	public void expireToken(String accessToken) {
		if (!isTokenValid(accessToken)) {
			throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
		}
		addTokenToBlacklist(accessToken);
	}

	private String removeBearerInToken(String token) {
		return token.startsWith(BEARER) ? token.substring(BEARER.length()).strip() : token;
	}

	private DecodedJWT verifyToken(String token) {
		return JWT.require(Algorithm.HMAC512(secretKey))
			.build()
			.verify(token);
	}

	public Optional<UserType> extractUserType(String token) {
		token = removeBearerInToken(token);
		try {
			String userTypeString = verifyToken(token).getClaim(USERTYPE_CLAIM).asString();
			return Optional.of(UserType.fromString(userTypeString));
		} catch (Exception e) {
			log.error("유효하지 않은 토큰입니다 : {}", token, e);
			return Optional.empty();
		}
	}

	public Optional<String> extractUserEmail(String token) {
		token = removeBearerInToken(token);
		try {
			String userEmail = verifyToken(token).getClaim(EMAIL_CLAIM).asString();
			return Optional.of(userEmail);
		} catch (Exception e) {
			log.error("유효하지 않은 토큰입니다 : {}", token, e);
			return Optional.empty();
		}
	}

	public Optional<Long> extractUserId(String token) {
		token = removeBearerInToken(token);
		try {
			Long userId = verifyToken(token).getClaim(ID_CLAIM).asLong();
			return Optional.of(userId);
		} catch (Exception e) {
			log.error("유효하지 않은 토큰입니다 : {}", token, e);
			return Optional.empty();
		}
	}
}
