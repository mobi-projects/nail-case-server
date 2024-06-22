package com.nailcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		jwtService.logoutAndBlacklistToken(accessToken);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/expire")
	public ResponseEntity<Void> expireToken(@RequestHeader("Authorization") String accessToken) {
		jwtService.expireToken(accessToken);
		return ResponseEntity.ok().build();
	}

}
