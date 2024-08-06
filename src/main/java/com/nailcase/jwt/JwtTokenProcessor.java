package com.nailcase.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.TokenErrorCode;
import com.nailcase.model.enums.TokenType;
import com.nailcase.oauth.dto.TokenResponseDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProcessor {
	private final JwtService jwtService;
	private final ExceptionHandler exceptionHandler;
	private final ObjectMapper objectMapper;
	private final CustomJwtAuthenticationManager customJwtAuthenticationManager;
	private static final String LOGOUT_URL = "/api/v1/auth/logout";
	private static final String REFRESH_TOKEN_URL = "/api/v1/auth/refresh";

	public boolean isLogoutRequest(String path) {
		return path.equals(LOGOUT_URL);
	}

	public boolean isRefreshTokenRequest(String path) {
		return path.equals(REFRESH_TOKEN_URL);
	}

	public void processLogout(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException {
		String accessToken = jwtService.extractAccessToken(request).orElse(null);
		String refreshToken = jwtService.extractRefreshToken(request).orElse(null);

		try {
			jwtService.logout(accessToken, refreshToken);
			// 로그아웃 성공 후 SecurityContext를 클리어합니다.
			SecurityContextHolder.clearContext();
			filterChain.doFilter(request, response);
		} catch (BusinessException e) {
			log.error("로그아웃 처리 중 비즈니스 예외 발생: {}", e.getMessage());
			exceptionHandler.handleException(response, e);
		} catch (ServletException e) {
			log.error("로그아웃 처리 중 서블릿 예외 발생", e);
			exceptionHandler.handleException(response, new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR));
		}
	}

	public TokenResponseDto processRefreshToken(HttpServletRequest request) throws BusinessException {
		String refreshToken = jwtService.extractRefreshToken(request)
			.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_NOT_FOUND));

		jwtService.validateTokenAndThrow(refreshToken, TokenType.REFRESH);
		return jwtService.refreshTokens(refreshToken);
	}

	public void processAccessToken(String accessToken) throws BusinessException {
		jwtService.validateTokenAndThrow(accessToken, TokenType.ACCESS);
		log.info("유효한 액세스 토큰, 인증 정보 저장 진행");
		customJwtAuthenticationManager.saveAuthentication(accessToken);
	}

	public void sendJsonResponse(HttpServletResponse response, Object body) throws IOException {
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(objectMapper.writeValueAsString(body));
	}
}
