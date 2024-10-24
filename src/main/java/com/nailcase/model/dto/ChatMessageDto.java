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
	private Long messageId;
	private Long chatRoomId;
	private Long shopId;
	private String message;
	private String sender;
	private LocalDateTime createdAt;

	public static ChatMessageDto of(ChatMessage chatMessage) {
		if (chatMessage == null) {
			return null;
		}

		ChatMessageDtoBuilder builder = ChatMessageDto.builder()
			.messageId(chatMessage.getChatMessageId())
			.message(chatMessage.getMessage())
			.sender(chatMessage.getWriter())
			.createdAt(chatMessage.getCreatedAt());

		if (chatMessage.getChatRoom() != null) {
			builder.chatRoomId(chatMessage.getChatRoom().getChatRoomId());

			if (chatMessage.getChatRoom().getShop() != null) {
				builder.shopId(chatMessage.getChatRoom().getShop().getShopId());
			}
		}

		return builder.build();
	}

	public ChatMessage toEntity() {
		return ChatMessage.builder()
			.message(this.getMessage())
			.writer(this.getSender())
			.build();
	}

	// ChatRoom 정보를 포함하여 엔티티 생성
	public ChatMessage toEntityWithRoom(ChatRoom chatRoom) {
		return ChatMessage.builder()
			.chatRoom(chatRoom)
			.message(this.getMessage())
			.writer(this.getSender())
			.build();
	}

	@Override
	public String toString() {
		return String.format("ChatMessageDto(messageId=%s, chatRoomId=%s, shopId=%s, writer=%s, message=%s)",
			messageId != null ? messageId.toString() : "null",
			chatRoomId != null ? chatRoomId.toString() : "null",
			shopId != null ? shopId.toString() : "null",
			sender,
			message);
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