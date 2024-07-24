package com.nailcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
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

	@GetMapping("/{userId}/info")
	public ResponseEntity<UserInfoResponse> getUserInfo(
		@PathVariable Long userId,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {

		// 권한 체크: 자신의 정보만 조회 가능하며, 역할도 일치해야 함
		if (!userPrincipal.getId().equals(userId) || !userService.isValidUserRole(userId, userPrincipal.getRole())) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}

		UserInfoResponse userInfo = userService.getUserInfo(userId, userPrincipal.getRole());
		return ResponseEntity.ok(userInfo);
	}
}