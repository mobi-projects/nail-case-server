package com.nailcase.model.dto;

import com.nailcase.model.entity.ChatRoom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDto {

	private Long chatRoomId;

	private String name;

	public static ChatRoomDto of(ChatRoom chatRoom) {
		return ChatRoomDto.builder()
			.chatRoomId(chatRoom.getChatRoomId())
			.name(chatRoom.getName())
			.build();
	}

	public ChatRoom toEntity() {
		return ChatRoom.builder()
			.name(name)
			.build();
	}
}