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

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.NailArtistDetails;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
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

	private static final String LOGOUT_URL = "/auth/logout";
	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;
	private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 추가

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {

		if (request.getRequestURI().equals(LOGOUT_URL)) {
			filterChain.doFilter(request, response);
			return;
		}

		String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);

		if (refreshToken != null) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}
		checkAccessTokenAndAuthentication(request, response, filterChain);
	}

	private void checkRefreshTokenAndReIssueAccessToken(@NonNull HttpServletResponse response,
		@NonNull String refreshToken) {
		jwtService.extractEmail(refreshToken).ifPresent(email -> {
			jwtService.extractRole(refreshToken).ifPresent(role -> {
				String savedRefreshToken = (String)redisTemplate.opsForValue().get(role.getKey() + ":" + email);
				if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
					String reIssuedRefreshToken = reIssueRefreshToken(email, role);
					if (role == MEMBER) {
						Member member = memberRepository.findByEmail(email)
							.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
						jwtService.sendAccessAndRefreshToken(response,
							jwtService.createAccessToken(email, member.getMemberId(), role),
							reIssuedRefreshToken);
					} else if (role == MANAGER) {
						NailArtist nailArtist = nailArtistRepository.findByEmail(email)
							.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
						jwtService.sendAccessAndRefreshToken(response,
							jwtService.createAccessToken(email, nailArtist.getNailArtistId(), role),
							reIssuedRefreshToken);
					} else {
						throw new BusinessException(AuthErrorCode.INVALID_USER_TYPE);
					}
				} else {
					throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
				}
			});
		});
	}

	public String reIssueRefreshToken(String email, com.nailcase.model.enums.Role role) {
		String newRefreshToken = jwtService.createRefreshToken(email, role);
		String key = role.getKey() + ":" + email;

		// 기존 토큰 제거
		redisTemplate.delete(key);

		// 새 토큰 저장
		boolean isSet = Boolean.TRUE.equals(redisTemplate.opsForValue()
			.setIfAbsent(key, newRefreshToken, jwtService.getRefreshTokenExpirationPeriod(), TimeUnit.MILLISECONDS));

		if (!isSet) {
			log.error("리프레시 토큰 저장 실패: {}", email);
			throw new BusinessException(AuthErrorCode.REFRESH_TOKEN_SAVE_FAILED);
		}

		log.info("리프레시 토큰 재발급 및 저장 성공: {}", email);
		return newRefreshToken;
	}

	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		log.info("checkAccessTokenAndAuthentication() 호출");
		jwtService.extractAccessToken(request)
			.filter(jwtService::isTokenValid)
			.ifPresent(this::saveAuthentication);

		filterChain.doFilter(request, response);
	}

	private void saveAuthentication(String token) {
		Role role = jwtService.extractRole(token)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
		Long userId = jwtService.extractUserId(token)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
		String email = jwtService.extractEmail(token)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));

		UserDetails userDetails;
		if (role == MEMBER) {
			Member member = memberRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
			userDetails = MemberDetails.withMember(member);
		} else if (role == MANAGER) {
			NailArtist nailArtist = nailArtistRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
			userDetails = NailArtistDetails.withNailArtist(nailArtist);
		} else {
			throw new BusinessException(AuthErrorCode.INVALID_USER_TYPE);
		}

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
