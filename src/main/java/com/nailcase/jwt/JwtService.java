package com.nailcase.jwt;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.TokenErrorCode;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.TokenType;
import com.nailcase.oauth.dto.TokenResponseDto;
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
	private static final String ROLE_CLAIM = "role";
	private static final String BEARER = "Bearer ";
	private static final String BLACKLIST_PREFIX = "blacklist:";
	private static final long ALLOWED_TIME_DIFFERENCE = 60000; // 1분

	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	public String createAccessToken(String email, Long sequenceId, Role role) {
		Date now = new Date();
		String jti = UUID.randomUUID().toString();
		String token = JWT.create()
			.withSubject(ACCESS_TOKEN_SUBJECT)
			.withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
			.withIssuedAt(now)
			.withClaim(EMAIL_CLAIM, email)
			.withClaim(ROLE_CLAIM, role.getKey())
			.withClaim(ID_CLAIM, sequenceId)
			.withJWTId(jti)
			.sign(Algorithm.HMAC512(secretKey));
		log.info("사용자 {}에 대한 액세스 토큰 발급", email);
		return token;
	}

	public String createRefreshToken(String email, Long sequenceId, Role role) {
		Date now = new Date();
		String jti = UUID.randomUUID().toString();
		String token = JWT.create()
			.withSubject(REFRESH_TOKEN_SUBJECT)
			.withClaim(EMAIL_CLAIM, email)
			.withClaim(ROLE_CLAIM, role.getKey())
			.withClaim(ID_CLAIM, sequenceId)
			.withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
			.withIssuedAt(now)
			.withJWTId(jti)
			.sign(Algorithm.HMAC512(secretKey));
		log.info("사용자 {}에 대한 리프레시 토큰 발급 (JTI: {})", email, jti);
		return token;
	}

	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader(accessHeader, accessToken);
		// log.info("새로 발급된 액세스 토큰: {}", accessToken);
	}

	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		setAccessTokenHeader(response, accessToken);
		setRefreshTokenHeader(response, refreshToken);
		log.info("액세스 토큰과 리프레시 토큰 헤더 설정 완료");
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
			throw new BusinessException(TokenErrorCode.TOKEN_INVALID);
		}
	}

	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(accessHeader, accessToken);
	}

	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(refreshHeader, refreshToken);
	}

	public void updateRefreshToken(String email, String refreshToken, Role role) {
		String key = role.getKey() + ":" + email;
		redisTemplate.opsForValue().set(key, refreshToken, refreshTokenExpirationPeriod, TimeUnit.MILLISECONDS);
		log.info("사용자 {}의 리프레시 토큰 업데이트", email);
	}

	public boolean isTokenValid(String token) {
		try {
			token = removeBearerInToken(token);
			DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
			String jti = decodedJWT.getId();
			if (jti == null) {
				log.warn("토큰에 JTI가 없습니다");
				return false;
			}
			String blacklistKey = BLACKLIST_PREFIX + jti;
			Boolean isBlacklisted = (Boolean)redisTemplate.opsForValue().get(blacklistKey);
			Date expiresAt = decodedJWT.getExpiresAt();
			log.info("토큰 검증 - JTI: {}, 블랙리스트 키: {}, 블랙리스트 여부: {}, 만료 시간: {}",
				jti, blacklistKey, isBlacklisted, expiresAt);

			if (isBlacklisted != null && isBlacklisted) {
				log.warn("블랙리스트에 등록된 토큰입니다: {}", token);
				return false;
			}

			boolean isValid = expiresAt.after(new Date());
			log.info("토큰 유효성 결과: {}", isValid);
			return isValid;
		} catch (BusinessException e) {
			log.error("토큰 검증 실패: {}", e.getMessage(), e);
			return false;
		} catch (Exception e) {
			log.error("예상치 못한 실패: {}", e.getMessage(), e);
			return false;
		}
	}

	public void logout(String accessToken, String refreshToken) {
		accessToken = removeBearerInToken(accessToken);
		refreshToken = removeBearerInToken(refreshToken);

		if (isTokenValid(accessToken)) {
			String email = extractEmail(accessToken)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
			Role role = extractRole(accessToken)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));

			String redisKey = role.getKey() + ":" + email;
			redisTemplate.delete(redisKey);
			addTokenToBlacklist(accessToken);
			log.info("사용자 {} 로그아웃 처리 완료", email);
		}

		if (isTokenValid(refreshToken)) {
			removeRefreshToken(refreshToken);
		}
	}

	public void removeRefreshToken(String refreshToken) {
		String email = extractEmail(refreshToken)
			.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
		Role role = extractRole(refreshToken)
			.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
		String key = role.getKey() + ":" + email;
		Boolean deleted = redisTemplate.delete(key);
		if (Boolean.FALSE.equals(deleted)) {
			log.warn("사용자 {}의 리프레시 토큰이 Redis에 없습니다", email);
		} else {
			log.info("사용자 {}의 리프레시 토큰 제거 완료", email);
		}
	}

	private String removeBearerInToken(String token) {
		return token.startsWith(BEARER) ? token.substring(BEARER.length()).strip() : token;
	}

	private DecodedJWT verifyToken(String token) {
		return JWT.require(Algorithm.HMAC512(secretKey))
			.build()
			.verify(token);
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

	public Optional<Role> extractRole(String token) {
		token = removeBearerInToken(token);
		try {
			String roleKey = verifyToken(token).getClaim(ROLE_CLAIM).asString();
			return Optional.of(Role.fromKey(roleKey));  // fromKey() 메소드 사용
		} catch (Exception e) {
			log.error("유효하지 않은 토큰입니다 : {}", token, e);
			return Optional.empty();
		}
	}

	public boolean isTokenValid(String token, TokenType tokenType) {
		try {
			validateTokenAndThrow(token, tokenType);
			return true;
		} catch (BusinessException e) {
			log.error("토큰 검증 실패: {}", e.getMessage(), e);
			return false;
		}
	}

	public void validateTokenAndThrow(String token, TokenType tokenType) {
		try {
			token = removeBearerInToken(token);
			DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);

			validateTokenClaims(decodedJWT, tokenType);
			validateTokenTimes(decodedJWT, tokenType);
			validateTokenBlacklist(decodedJWT);

		} catch (TokenExpiredException e) {
			throw new BusinessException(tokenType == TokenType.ACCESS ?
				TokenErrorCode.ACCESS_TOKEN_EXPIRED : TokenErrorCode.REFRESH_TOKEN_EXPIRED);
		} catch (SignatureVerificationException e) {
			throw new BusinessException(TokenErrorCode.TOKEN_SIGNATURE_INVALID);
		} catch (JWTDecodeException e) {
			throw new BusinessException(TokenErrorCode.TOKEN_MALFORMED);
		} catch (AlgorithmMismatchException e) {
			throw new BusinessException(TokenErrorCode.TOKEN_UNSUPPORTED);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new BusinessException(TokenErrorCode.TOKEN_INVALID);
		}
	}

	private void validateTokenClaims(DecodedJWT decodedJWT, TokenType tokenType) {
		String jti = decodedJWT.getId();
		if (jti == null) {
			throw new BusinessException(TokenErrorCode.NOT_JTI_IN_TOKEN);
		}

		if (tokenType == TokenType.ACCESS && !decodedJWT.getSubject().equals(ACCESS_TOKEN_SUBJECT)) {
			throw new BusinessException(TokenErrorCode.TOKEN_INVALID);
		} else if (tokenType == TokenType.REFRESH && !decodedJWT.getSubject().equals(REFRESH_TOKEN_SUBJECT)) {
			throw new BusinessException(TokenErrorCode.TOKEN_INVALID);
		}
	}

	private void validateTokenTimes(DecodedJWT decodedJWT, TokenType tokenType) {
		Date now = new Date();
		Date issuedAt = decodedJWT.getIssuedAt();
		Date expiresAt = decodedJWT.getExpiresAt();

		if (issuedAt != null && issuedAt.after(new Date(now.getTime() + ALLOWED_TIME_DIFFERENCE))) {
			throw new BusinessException(TokenErrorCode.TOKEN_ISSUED_IN_FUTURE);
		}

		if (expiresAt.before(now)) {
			if (tokenType == TokenType.ACCESS) {
				throw new BusinessException(TokenErrorCode.ACCESS_TOKEN_EXPIRED);
			} else {
				throw new BusinessException(TokenErrorCode.REFRESH_TOKEN_EXPIRED);
			}
		}
	}

	private void validateTokenBlacklist(DecodedJWT decodedJWT) {
		String blacklistKey = BLACKLIST_PREFIX + decodedJWT.getId();
		Boolean isBlacklisted = (Boolean)redisTemplate.opsForValue().get(blacklistKey);

		if (isBlacklisted != null && isBlacklisted) {
			throw new BusinessException(TokenErrorCode.TOKEN_IN_BLACKLIST);
		}
	}

	public void addTokenToBlacklist(String token) {
		token = removeBearerInToken(token);
		DecodedJWT decoded = verifyToken(token);
		long remainingTime = decoded.getExpiresAt().getTime() - System.currentTimeMillis();
		redisTemplate.opsForValue().set(BLACKLIST_PREFIX + decoded.getId(), true, remainingTime, TimeUnit.MILLISECONDS);
		log.info("토큰을 블랙리스트에 추가: JWT ID {}", decoded.getId());
	}

	public TokenResponseDto refreshTokens(String refreshToken) {
		validateTokenAndThrow(refreshToken, TokenType.REFRESH);

		String email = extractEmail(refreshToken)
			.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
		Role role = extractRole(refreshToken)
			.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));

		String savedRefreshToken = (String)redisTemplate.opsForValue().get(role.getKey() + ":" + email);
		if (savedRefreshToken == null) {
			throw new BusinessException(TokenErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}
		if (!savedRefreshToken.equals(refreshToken)) {
			// 리프레시 토큰 재사용 감지
			removeRefreshToken(refreshToken);
			throw new BusinessException(TokenErrorCode.REFRESH_TOKEN_REUSED);
		}

		Long userId = extractUserId(refreshToken)
			.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));

		String newAccessToken = createAccessToken(email, userId, role);
		String newRefreshToken = createRefreshToken(email, userId, role);

		updateRefreshToken(email, newRefreshToken, role);

		return TokenResponseDto.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();
	}

	public long getAccessTokenExpirationPeriod() {
		return accessTokenExpirationPeriod / 1000;
	}

	public long getRefreshTokenExpirationPeriod() {
		return refreshTokenExpirationPeriod / 1000;
	}
}
