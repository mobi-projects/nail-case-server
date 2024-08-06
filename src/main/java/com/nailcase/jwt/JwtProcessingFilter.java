package com.nailcase.jwt;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nailcase.oauth.dto.TokenResponseDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtProcessingFilter extends OncePerRequestFilter {

	private final JwtTokenProcessor jwtTokenProcessor;
	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
		@NotNull FilterChain filterChain)
		throws ServletException, IOException {
		log.info("JWT 인증 처리 필터: URI {} 처리 중", request.getRequestURI());

		if (jwtTokenProcessor.isLogoutRequest(request.getRequestURI())) {
			log.info("로그아웃 URL 감지, 로그아웃 처리");
			jwtTokenProcessor.processLogout(request, response, filterChain);
			return;
		}

		if (jwtTokenProcessor.isRefreshTokenRequest(request.getRequestURI())) {
			log.info("토큰 갱신 URL 감지, 토큰 갱신 처리");
			TokenResponseDto tokenResponse = jwtTokenProcessor.processRefreshToken(request);
			jwtTokenProcessor.sendJsonResponse(response, tokenResponse);
			return;
		}

		String accessToken = jwtService.extractAccessToken(request).orElse(null);
		if (accessToken != null) {
			jwtTokenProcessor.processAccessToken(accessToken);
		} else {
			log.info("요청에서 액세스 토큰을 찾을 수 없음");
		}

		filterChain.doFilter(request, response);
	}
}
