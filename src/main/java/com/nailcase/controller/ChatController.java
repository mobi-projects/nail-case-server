package com.nailcase.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.service.ChatRoomService;
import com.nailcase.service.ChattingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

	private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
	private final ChattingService chattingService;
	private final ChatRoomService chatRoomService;
	//Client가 SEND할 수 있는 경로
	//stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
	//"/pub/chat/enter"

	@MessageMapping("/chat/enter")
	public void enter(ChatMessageDto message) {
		log.info("@ChatController, enter()");
		if (!chatRoomService.existsByChatRoomId(message.getChatRoomId())) {
			log.error("Chat room not found for id: {}", message.getChatRoomId());
			return; // 채팅방이 없으면 처리 중단
		}
		template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
	}

	@MessageMapping("/chat/message")
	public void message(ChatMessageDto message) {
		log.info("@ChatController, message()");
		if (!chatRoomService.existsByChatRoomId(message.getChatRoomId())) {
			log.error("Chat room not found for id: {}", message.getChatRoomId());
			return; // 채팅방이 없으면 처리 중단
		}

		chattingService.saveMessage(message);
		template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
	}

	@GetMapping("/chat")
	public String chatGET() {

		log.info("@ChatController, chat GET()");

		return "chat";
	}

}