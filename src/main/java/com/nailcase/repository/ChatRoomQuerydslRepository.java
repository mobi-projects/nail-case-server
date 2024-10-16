package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import com.nailcase.model.entity.ChatRoom;

public interface ChatRoomQuerydslRepository {
	List<ChatRoom> findAllByChatRoomId(Long chatRoomId);

	Optional<ChatRoom> findByChatRoomId(Long chatRoomId);

	boolean existsByChatRoomId(Long chatRoomId);

}
