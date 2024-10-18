package com.nailcase.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

	private Long chatRoomId;
	private String writer;
	private String message;
	private LocalDateTime createdAt;

	public static ChatMessageDto of(ChatMessage chatMessage) {
		return ChatMessageDto.builder()
			.chatRoomId(chatMessage.getChatRoom().getChatRoomId())
			.writer(chatMessage.getWriter())
			.message(chatMessage.getMessage())
			.createdAt(chatMessage.getCreatedAt())
			.build();
	}

	public ChatMessage toEntity(ChatMessageDto messageDto) {
		return ChatMessage.builder()
			.chatRoom(ChatRoom.builder().chatRoomId(messageDto.getChatRoomId()).build())
			.writer(messageDto.getWriter())
			.message(messageDto.getMessage())
			.build();
	}

	@Data
	@AllArgsConstructor
	public static class PageableResponse {
		private List<ChatMessageDto> chatMessageList;
		private int pageNumber;
		private int pageSize;
		private long totalElements;
		private int totalPages;
		private boolean last;
	}
}