package com.nailcase.repository;

import java.util.List;

import com.nailcase.model.entity.ChatMessage;

public interface ChattingQuerydslRepository {

	List<ChatMessage> findMessagesByChatRoomId(Long chatRoomId);

}
