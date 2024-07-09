package com.nailcase.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.service.AbstractKakaoLoginService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AbstractKakaoLoginService kakaoMemberLoginService;
	private final AbstractKakaoLoginService kakaoManagerLoginService;

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