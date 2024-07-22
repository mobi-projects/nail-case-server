package com.nailcase.jwt.filter;

import static com.nailcase.model.enums.Role.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.ErrorResponse;
import com.nailcase.exception.codes.NailArtistErrorCode;
import com.nailcase.exception.codes.TokenErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.NailArtistDetails;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.TokenType;
import com.nailcase.oauth.dto.TokenResponseDto;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;
import com.nailcase.response.ResponseService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private static final String LOGOUT_URL = "/api/v1/auth/logout";
	private static final String REFRESH_TOKEN_URL = "/api/v1/auth/refresh";

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;
	private final ResponseService responseService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException, BusinessException {
		try {
			log.info("JWT 인증 처리 필터: URI {} 처리 중", request.getRequestURI());

			if (request.getRequestURI().equals(LOGOUT_URL)) {
				log.info("로그아웃 URL 감지, 로그아웃 처리");
				processLogout(request, response, filterChain);
				return;
			}

			if (request.getRequestURI().equals(REFRESH_TOKEN_URL)) {
				log.info("리프레시 토큰 URL 감지, 리프레시 토큰 처리");
				TokenResponseDto tokenResponse = processRefreshToken(request);
				sendJsonResponse(response, tokenResponse);
				return;
			}

			String accessToken = jwtService.extractAccessToken(request).orElse(null);

			if (accessToken != null) {
				processAccessToken(accessToken);
			} else {
				log.info("요청에서 액세스 토큰을 찾을 수 없음");
			}
		} catch (BusinessException ex) {
			log.error("JWT 처리 중 비즈니스 예외 발생: {}", ex.getMessage());
			handleException(response, ex);
			return;
		} catch (Exception ex) {
			log.error("JWT 처리 중 예외 발생: {}", ex.getMessage());
			handleException(response, new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR));
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void processAccessToken(String accessToken) throws BusinessException {
		jwtService.validateTokenAndThrow(accessToken, TokenType.ACCESS);
		log.info("유효한 액세스 토큰, 인증 정보 저장 진행");
		saveAuthentication(accessToken);
	}

	private TokenResponseDto processRefreshToken(HttpServletRequest request) throws BusinessException {
		String refreshToken = jwtService.extractRefreshToken(request)
			.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_NOT_FOUND));

		jwtService.validateTokenAndThrow(refreshToken, TokenType.REFRESH);
		return reissueTokens(refreshToken);
	}

	private void processLogout(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
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
			handleException(response, e);
		} catch (ServletException e) {
			log.error("로그아웃 처리 중 서블릿 예외 발생", e);
			handleException(response, new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR));
		}
	}

	private void saveAuthentication(String token) {
		try {
			Role role = jwtService.extractRole(token)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
			Long userId = jwtService.extractUserId(token)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
			String email = jwtService.extractEmail(token)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));

			log.info("토큰에서 추출된 정보 - 역할: {}, 사용자 ID: {}, 이메일: {}", role, userId, email);

			UserPrincipal userPrincipal = createUserPrincipal(role, userId);

			Authentication authentication = new UsernamePasswordAuthenticationToken(
				userPrincipal, null, ((UserDetails)userPrincipal).getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("SecurityContextHolder에 인증 정보 설정 완료: {}", authentication);
		} catch (Exception e) {
			log.error("인증 정보 저장 실패: {}", e.getMessage(), e);
			throw new BusinessException(AuthErrorCode.AUTHENTICATION_FAILED);
		}
	}

	private UserPrincipal createUserPrincipal(Role role, Long userId) {
		if (role == MEMBER) {
			Member member = memberRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
			return MemberDetails.withMember(member);
		} else if (role == MANAGER) {
			NailArtist nailArtist = nailArtistRepository.findByIdWithShops(userId)
				.orElseThrow(() -> new BusinessException(NailArtistErrorCode.NOT_FOUND));
			return NailArtistDetails.withNailArtist(nailArtist);
		} else {
			throw new BusinessException(AuthErrorCode.INVALID_USER_TYPE);
		}
	}

	private TokenResponseDto reissueTokens(String refreshToken) {
		return jwtService.refreshTokens(refreshToken);
	}

	private void handleException(HttpServletResponse response, BusinessException ex) throws IOException {
		HttpStatus status;
		if (ex.getErrorCode() instanceof TokenErrorCode) {
			status = HttpStatus.UNAUTHORIZED;
		} else if (ex.getErrorCode() instanceof UserErrorCode) {
			status = HttpStatus.BAD_REQUEST;
		} else {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ErrorResponse errorResponse = responseService.getErrorResponse(ex.getErrorCode(), status.value());
		String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}

	private void sendJsonResponse(HttpServletResponse response, Object body) throws IOException {
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
	}
}