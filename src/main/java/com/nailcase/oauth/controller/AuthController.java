package com.nailcase.oauth.controller;

import static org.hibernate.query.sqm.tree.SqmNode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.ErrorResponse;
import com.nailcase.jwt.JwtService;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.LogoutResponseDto;
import com.nailcase.oauth.dto.TokenResponseDto;
import com.nailcase.oauth.service.AbstractKakaoLoginService;
import com.nailcase.response.ResponseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AbstractKakaoLoginService kakaoMemberLoginService;
	private final AbstractKakaoLoginService kakaoManagerLoginService;
	private final JwtService jwtService;
	private final ResponseService responseService;

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken,
		@RequestHeader("Refresh-Token") String refreshToken) {
		try {
			jwtService.logout(accessToken, refreshToken);
			return ResponseEntity.ok(new LogoutResponseDto());
		} catch (BusinessException e) {
			log.error("로그아웃 처리 중 오류 발생", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
		try {
			TokenResponseDto tokenResponse = jwtService.refreshTokens(refreshToken);
			return ResponseEntity.ok()
				.header(jwtService.getAccessHeader(), "Bearer " + tokenResponse.getAccessToken())
				.header(jwtService.getRefreshHeader(), "Bearer " + tokenResponse.getRefreshToken())
				.body(responseService.getSingleResponse(tokenResponse));
		} catch (BusinessException e) {
			log.error("토큰 갱신 중 오류 발생", e);
			ErrorResponse errorResponse = responseService.getErrorResponse(e.getErrorCode(),
				HttpStatus.UNAUTHORIZED.value());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
		}
	}

	@GetMapping("/{userType}/{service}")
	public LoginResponseDto socialLogin(
		@PathVariable String userType,
		@PathVariable String service,
		@RequestParam String code) {

		AbstractKakaoLoginService loginService = getLoginService(userType, service);
		return loginService.processLogin(code);
	}

	private AbstractKakaoLoginService getLoginService(String userType, String service) {
		if ("kakao".equalsIgnoreCase(service)) {
			if ("member".equalsIgnoreCase(userType)) {
				return kakaoMemberLoginService;
			} else if ("manager".equalsIgnoreCase(userType)) {
				return kakaoManagerLoginService;
			}
		}
		throw new BusinessException(AuthErrorCode.AUTH_UNSUPPORTED);
	}
}