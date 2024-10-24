package com.nailcase.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;

	@MessageMapping("/{shopId}/chat/message/{chatRoomId}")
	public void message(
		@DestinationVariable Long shopId,
		@Payload ChatMessageDto message,
		@DestinationVariable Long chatRoomId  // URL 변수는 @DestinationVariable 사용
	) {
		log.info("Received message for shopId: {}, chatRoomId: {}, from sender: {}", shopId, chatRoomId,
			message.getSender());
		try {
			chatRoomService.saveAndSendMessage(shopId, message, chatRoomId);
		} catch (Exception e) {
			log.error("Error processing message: ", e);
			throw e; // Or handle more gracefully depending on your application's needs
		}
	}

	@GetMapping("/{shopId}/chat/room")
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

	@GetMapping("/{shopId}/chat/room/{chatRoomId}")
	public ResponseEntity<ChatMessageDto.PageableResponse> managerEnterRoom(
		@PathVariable Long shopId,
		@PathVariable Long chatRoomId,
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