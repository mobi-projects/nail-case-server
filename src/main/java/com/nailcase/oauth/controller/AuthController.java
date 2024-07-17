package com.nailcase.oauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.LogoutResponseDto;
import com.nailcase.oauth.service.AbstractKakaoLoginService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AbstractKakaoLoginService kakaoMemberLoginService;
	private final AbstractKakaoLoginService kakaoManagerLoginService;

	@Autowired
	public AuthController(
		@Qualifier("kakaoMemberLoginService") AbstractKakaoLoginService kakaoMemberLoginService,
		@Qualifier("kakaoManagerLoginService") AbstractKakaoLoginService kakaoManagerLoginService) {
		this.kakaoMemberLoginService = kakaoMemberLoginService;
		this.kakaoManagerLoginService = kakaoManagerLoginService;
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok(new LogoutResponseDto());
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