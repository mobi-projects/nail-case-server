package com.nailcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.response.UserInfoResponse;
import com.nailcase.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/info")
	public ResponseEntity<UserInfoResponse> getUserInfo(
		@AuthenticationPrincipal UserPrincipal userPrincipal) {

		UserInfoResponse userInfo = userService.getUserInfo(userPrincipal.id(), userPrincipal.role());
		return ResponseEntity.ok(userInfo);
	}
}