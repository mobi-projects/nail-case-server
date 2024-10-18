package com.nailcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shops/{shopId}")
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;

	@MessageMapping("/chat/message/{chatRoomId}")
	public void message(
		@PathVariable Long shopId,
		ChatMessageDto message, @DestinationVariable String chatRoomId,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		chatRoomService.saveAndSendMessage(shopId, message, chatRoomId);
	}

	@GetMapping("/chat/room")
	public ResponseEntity<ChatMessageDto.PageableResponse> memberEnterRoom(
		@PathVariable Long shopId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		ChatMessageDto.PageableResponse response = chatRoomService.enterShopChatRoom(userPrincipal, shopId, page,
			size);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/chat/room/{chatRoomId}")
	public ResponseEntity<ChatMessageDto.PageableResponse> managerEnterRoom(
		@PathVariable Long shopId,
		@PathVariable String chatRoomId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		ChatMessageDto.PageableResponse response = chatRoomService.managerEnterRoom(userPrincipal, shopId, chatRoomId,
			page,
			size);
		return ResponseEntity.ok(response);
	}
}