package com.nailcase.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/pub/shops/{shopId}")
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;

	@MessageMapping("/chat/message/{chatRoomId}")
	public void message(
		@PathVariable Long shopId,
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

}