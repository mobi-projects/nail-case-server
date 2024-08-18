package com.nailcase.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.TokenErrorCode;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.enums.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomJwtAuthenticationManager {

	private final JwtService jwtService;
	private final CustomUserDetailsService customUserDetailsService;

	public void saveAuthentication(String token) {
		try {
			Role role = jwtService.extractRole(token)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
			Long userId = jwtService.extractUserId(token)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));
			String email = jwtService.extractEmail(token)
				.orElseThrow(() -> new BusinessException(TokenErrorCode.TOKEN_INVALID));

			// log.info("토큰에서 추출된 정보 - 역할: {}, 사용자 ID: {}, 이메일: {}", role, userId, email);

			UserPrincipal userPrincipal = customUserDetailsService.createUserPrincipalDetails(role, userId);

			Authentication authentication = new UsernamePasswordAuthenticationToken(
				userPrincipal, null, ((UserDetails)userPrincipal).getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// log.info("SecurityContextHolder에 인증 정보 설정 완료: {}", authentication);
		} catch (Exception e) {
			log.error("인증 정보 저장 실패: {}", e.getMessage(), e);
			throw new BusinessException(AuthErrorCode.AUTHENTICATION_FAILED);
		}
	}
}
