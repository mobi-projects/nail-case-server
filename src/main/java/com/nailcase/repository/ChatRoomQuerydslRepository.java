package com.nailcase.repository;

import com.nailcase.model.entity.ChatRoom;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.ChatRoomStatus;

import java.util.List;
import java.util.Optional;

public interface ChatRoomQuerydslRepository {

    Optional<ChatRoom> findByShopAndMember(Shop shop, Member member);

    List<ChatRoom> findByMemberIdAndChatRoomStatus(Long memberId, ChatRoomStatus status);

    Optional<ChatRoom> findByChatRoomIdWithDetails(Long chatRoomId);


    List<ChatRoom> findByShopIdAndChatRoomStatus(Long shopId, ChatRoomStatus status);

}
