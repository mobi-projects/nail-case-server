package com.nailcase.model.dto;

import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.ChatRoom;
import com.nailcase.util.DateUtils;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDto {

    private Long chatRoomId;
    private Long shopId;
    private Long memberId;
    private String name;
    private Long createdAt;
    private Long lastMessageTime; // 마지막 메시지 시간
    private String lastMessage; // 마지막 메시지 내용
    private Long unreadCount; // 안 읽은 메시지 수
    private Boolean isLastMessageFromShop;  // 마지막 메시지 발신자 타입 추가


    public static ChatRoomDto of(ChatRoom chatRoom, boolean isShopView) {
        ChatMessage lastMessage = chatRoom.getLastMessage();

        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .shopId(chatRoom.getShop().getShopId())
                .memberId(chatRoom.getMember().getMemberId())
                .name(chatRoom.getName())
                .createdAt(DateUtils.localDateTimeToUnixTimeStampWithNull(chatRoom.getCreatedAt()))
                .lastMessageTime(DateUtils.localDateTimeToUnixTimeStampWithNull(lastMessage != null ? lastMessage.getCreatedAt() : null))
                .lastMessage(lastMessage != null ? lastMessage.getMessage() : null)
                .isLastMessageFromShop(lastMessage != null ? lastMessage.isSentByShop() : null)  // 마지막 메시지 발신자 정보 추가
                .unreadCount(isShopView ?
                        chatRoom.getUnreadMessageCountForShop() :
                        chatRoom.getUnreadMessageCountForMember(chatRoom.getMember().getMemberId()))
                .build();


    }

    public static ChatRoomDto of(ChatRoom chatRoom) {
        return of(chatRoom, false);
    }

}