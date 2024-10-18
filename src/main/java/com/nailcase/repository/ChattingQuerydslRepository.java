package com.nailcase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.ChatMessage;

public interface ChattingQuerydslRepository {

	Page<ChatMessage> findByChatRoomIdWithPagination(Long chatRoomId, Pageable pageable);
}
