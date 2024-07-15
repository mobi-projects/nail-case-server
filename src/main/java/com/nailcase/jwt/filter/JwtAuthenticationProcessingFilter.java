package com.nailcase.jwt.filter;

import static com.nailcase.model.enums.Role.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.exception.TokenException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.NailArtistDetails;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.oauth.dto.TokenResponseDto;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;

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

	private static final String LOGOUT_URL = "api/v1/auth/logout";
	private static final String REFRESH_TOKEN_URL = "/api/v1/auth/refresh";

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException, TokenException {

		log.info("JWT 인증 처리 필터: URI {} 처리 중", request.getRequestURI());

		if (request.getRequestURI().equals(LOGOUT_URL)) {
			log.info("로그아웃 URL 감지, 토큰 처리 건너뛰기");
			filterChain.doFilter(request, response);
			return;
		}

		if (request.getRequestURI().equals(REFRESH_TOKEN_URL)) {
			log.info("리프레시 토큰 URL 감지, 리프레시 토큰 처리");
			processRefreshToken(request, response);
			return;
		}

		String accessToken = jwtService.extractAccessToken(request).orElse(null);
		log.info("추출된 액세스 토큰: {}", accessToken);

		if (accessToken != null) {
			processAccessToken(accessToken, response);
		} else {
			log.info("요청에서 액세스 토큰을 찾을 수 없음");
		}

		filterChain.doFilter(request, response);
	}

	private void processAccessToken(String accessToken, HttpServletResponse response) throws
		IOException, TokenException {

		if (jwtService.isTokenValid(accessToken)) {
			log.info("유효한 액세스 토큰, 인증 정보 저장 진행");
			saveAuthentication(accessToken);
		} else {
			throw new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage());
		}
	}

	private void processRefreshToken(HttpServletRequest request, HttpServletResponse response) throws
		IOException,
		TokenException {
		String refreshToken = jwtService.extractRefreshToken(request).orElse(null);
		if (refreshToken != null && jwtService.isTokenValid(refreshToken)) {
			try {
				TokenResponseDto tokenResponse = reissueTokens(refreshToken);
				response.setContentType("application/json");
				response.getWriter().write(new ObjectMapper().writeValueAsString(tokenResponse));
			} catch (TokenException e) {
				throw new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage());
			}
		} else {
			throw new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage());
		}
	}

	private void saveAuthentication(String token) {
		try {
			Role role = jwtService.extractRole(token)
				.orElseThrow(() -> new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage()));
			Long userId = jwtService.extractUserId(token)
				.orElseThrow(() -> new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage()));
			String email = jwtService.extractEmail(token)
				.orElseThrow(() -> new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage()));

			log.info("토큰에서 추출된 정보 - 역할: {}, 사용자 ID: {}, 이메일: {}", role, userId, email);

			UserPrincipal userPrincipal = createUserPrincipal(role, userId);

			Authentication authentication = new UsernamePasswordAuthenticationToken(
				userPrincipal, null, ((UserDetails)userPrincipal).getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("SecurityContextHolder에 인증 정보 설정 완료: {}", authentication);
		} catch (Exception e) {
			log.error("인증 정보 저장 실패: {}", e.getMessage(), e);
		}
	}

	private UserPrincipal createUserPrincipal(Role role, Long userId) {
		if (role == MEMBER) {
			Member member = memberRepository.findById(userId)
				.orElseThrow(() -> new TokenException(UserErrorCode.USER_NOT_FOUND.getMessage()));
			return MemberDetails.withMember(member);
		} else if (role == MANAGER) {
			NailArtist nailArtist = nailArtistRepository.findById(userId)
				.orElseThrow(() -> new TokenException(UserErrorCode.USER_NOT_FOUND.getMessage()));
			return NailArtistDetails.withNailArtist(nailArtist);
		} else {
			throw new TokenException(AuthErrorCode.INVALID_USER_TYPE.getMessage());
		}
	}

	public String reIssueRefreshToken(String email, Long userId, Role role) {
		String newRefreshToken = jwtService.createRefreshToken(email, userId, role);
		String key = role.getKey() + ":" + email;

		redisTemplate.delete(key);

		boolean isSet = Boolean.TRUE.equals(redisTemplate.opsForValue()
			.setIfAbsent(key, newRefreshToken, jwtService.getRefreshTokenExpirationPeriod(), TimeUnit.MILLISECONDS));

		if (!isSet) {
			log.error("리프레시 토큰 저장 실패: {}", email);
			throw new TokenException(AuthErrorCode.REFRESH_TOKEN_SAVE_FAILED.getMessage());
		}

		log.info("리프레시 토큰 재발급 및 저장 성공: {}", email);
		return newRefreshToken;
	}

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		jwtService.extractEmail(refreshToken).ifPresent(email -> {
			jwtService.extractRole(refreshToken).ifPresent(role -> {
				String savedRefreshToken = (String)redisTemplate.opsForValue().get(role.getKey() + ":" + email);
				if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
					String reIssuedRefreshToken = reIssueRefreshToken(email, getUserId(email, role), role);
					reissueTokens(response, email, role, reIssuedRefreshToken);
				} else {
					throw new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage());
				}
			});
		});
	}

	private void reissueTokens(HttpServletResponse response, String email, Role role, String refreshToken) {
		if (role == MEMBER) {
			Member member = memberRepository.findByEmail(email)
				.orElseThrow(() -> new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage()));
			jwtService.sendAccessAndRefreshToken(response,
				jwtService.createAccessToken(email, member.getMemberId(), role),
				refreshToken);
		} else if (role == MANAGER) {
			NailArtist nailArtist = nailArtistRepository.findByEmail(email)
				.orElseThrow(() -> new TokenException(AuthErrorCode.TOKEN_INVALID.getMessage()));
			jwtService.sendAccessAndRefreshToken(response,
				jwtService.createAccessToken(email, nailArtist.getNailArtistId(), role),
				refreshToken);
		} else {
			throw new TokenException(AuthErrorCode.INVALID_USER_TYPE.getMessage());
		}
	}

	private TokenResponseDto reissueTokens(String refreshToken) {
		return jwtService.refreshTokens(refreshToken);
	}

	private Long getUserId(String email, Role role) {
		if (role == MEMBER) {
			return memberRepository.findByEmail(email)
				.orElseThrow(() -> new TokenException(UserErrorCode.USER_NOT_FOUND.getMessage()))
				.getMemberId();
		} else if (role == MANAGER) {
			return nailArtistRepository.findByEmail(email)
				.orElseThrow(() -> new TokenException(UserErrorCode.USER_NOT_FOUND.getMessage()))
				.getNailArtistId();
		}
		throw new TokenException(AuthErrorCode.INVALID_USER_TYPE.getMessage());
	}

	private void updateRefreshTokenInRedis(String email, Role role, String newRefreshToken) {
		String key = role.getKey() + ":" + email;
		redisTemplate.opsForValue()
			.set(key, newRefreshToken, jwtService.getRefreshTokenExpirationPeriod(), TimeUnit.MILLISECONDS);
	}

}