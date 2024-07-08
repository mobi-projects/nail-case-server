package com.nailcase.oauth2.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.oauth2.dto.LoginResponseDto;
import com.nailcase.oauth2.service.SocialLoginService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OAuth2Controller {

	private final Map<String, SocialLoginService> loginServices;

	@GetMapping("/{service}")
	public LoginResponseDto socialLogin(@PathVariable String service, @RequestParam String code) {
		SocialLoginService loginService = loginServices.get(service + "LoginService");
		if (loginService == null) {
			throw new BusinessException(AuthErrorCode.AUTH_UNSUPPORTED);
		}
		return loginService.processLogin(code);
	}

}