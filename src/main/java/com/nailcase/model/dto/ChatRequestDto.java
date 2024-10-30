package com.nailcase.model.dto;

import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.ChatRoom;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {
    private Long chatRoomId;
    private Long memberId;
    private Long shopId;
    private String message;
    private boolean shopMessage;

    public ChatMessage toEntity(ChatRoom chatRoom, Member member, Shop shop) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(member)
                .shop(shop)
                .message(message)
                .sentByShop(shopMessage)
                .build();
    }

}
