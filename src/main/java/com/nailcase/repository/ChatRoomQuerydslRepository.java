package com.nailcase.repository;

import java.util.Optional;

import com.nailcase.model.entity.ChatRoom;

public interface ChatRoomQuerydslRepository {

	boolean existsByShopIdAndChatRoomId(Long shopId, Long chatRoomId);

	Optional<ChatRoom> findChatRoomByShopIdAndMemberId(Long shopId, Long memberId);

	Optional<ChatRoom> findChatRoomByShopIdAndChatRoomId(Long shopId, Long chatRoomId);
}
