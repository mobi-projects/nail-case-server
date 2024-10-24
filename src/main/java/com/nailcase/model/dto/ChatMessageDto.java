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

	private Long shopId;
	private Long chatRoomId;
	private String sender;        // writer를 sender로 변경
	private String content;
	private LocalDateTime createdAt;

	public static ChatMessageDto of(ChatMessage chatMessage) {
		return ChatMessageDto.builder()
			.chatRoomId(chatMessage.getChatRoom().getChatRoomId())
			.sender(chatMessage.getWriter())
			.content(chatMessage.getMessage())
			.createdAt(chatMessage.getCreatedAt())
			.build();
	}

	public ChatMessage toEntity(ChatMessageDto messageDto) {
		return ChatMessage.builder()
			.chatRoom(ChatRoom.builder().chatRoomId(messageDto.getChatRoomId()).build())
			.writer(messageDto.getSender())
			.message(messageDto.getContent())
			.build();
	}

	@Data
	@AllArgsConstructor
	public static class PageableResponse {
		private Long shopId;
		private Long chatRoomId;
		private List<ChatMessageDto> chatMessageList;
		private int pageNumber;
		private int pageSize;
		private long totalElements;
		private int totalPages;
		private boolean last;
	}
}