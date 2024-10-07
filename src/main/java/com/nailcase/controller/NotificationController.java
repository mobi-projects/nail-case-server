package com.nailcase.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.nailcase.model.dto.NotificationDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.service.NotificationService;

import lombok.RequiredArgsConstructor;

// NotificationController.java
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationService notificationService;

	@GetMapping("/subscribe")
	public SseEmitter subscribe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		return notificationService.connectNotification(userPrincipal);
	}

	@GetMapping("/list")
	public ResponseEntity<List<NotificationDto.Response.GetListResponse>> getNotifications(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size) {
		List<NotificationDto.Response.GetListResponse> notifications = notificationService.getNotifications(
			userPrincipal.id(), userPrincipal.role(), page, size);
		return ResponseEntity.ok(notifications);
	}

}
