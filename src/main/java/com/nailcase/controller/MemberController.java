package com.nailcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.jwt.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

	private final JwtService jwtService;

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String accessToken) {
		String token = accessToken.replace("Bearer ", "");
		if (!jwtService.isTokenValid(token)) {
			throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
		}
		String email = jwtService.extractEmail(token)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
		jwtService.logoutUser(email);
		jwtService.addTokenToBlacklist(token);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/expire")
	public ResponseEntity<Void> addTokenToBlacklist(@RequestHeader("Authorization") String accessToken) {
		String token = accessToken.replace("Bearer ", "");
		if (!jwtService.isTokenValid(token)) {
			throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
		}
		jwtService.addTokenToBlacklist(token);
		return ResponseEntity.ok().build();
	}

}
