package com.nailcase.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.service.ChatRoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/pub/shops")
@RequiredArgsConstructor
public class ChatController {

	private final ChatRoomService chatRoomService;

	@MessageMapping("/chat/message")
	public void message(
		@Payload ChatMessageDto message) {
		try {
			chatRoomService.saveAndSendMessage(message);
		} catch (Exception e) {
			log.error("Error processing message: ", e);
			throw e; // Or handle more gracefully depending on your application's needs
		}
	}

}