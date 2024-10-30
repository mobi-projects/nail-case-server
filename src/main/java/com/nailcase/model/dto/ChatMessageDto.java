package com.nailcase.model.dto;

import com.nailcase.model.entity.ChatMessage;
import com.nailcase.util.DateUtils;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long chatMessageId;
    private Long chatRoomId;
    private Long memberId;
    private Long shopId;
    private String message;
    private boolean readByMember;
    private boolean readByShop;
    private boolean sentByShop;
    private Long createdAt;
    private String writerNickname;

    public static ChatMessageDto of(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .chatMessageId(chatMessage.getChatMessageId())
                .chatRoomId(chatMessage.getChatRoom().getChatRoomId())
                .memberId(chatMessage.getMember().getMemberId())
                .shopId(chatMessage.getShop().getShopId())
                .message(chatMessage.getMessage())
                .readByMember(chatMessage.isReadByMember())  // isRead -> isReadByMember
                .readByShop(chatMessage.isReadByShop())      // 추가
                .createdAt(DateUtils.localDateTimeToUnixTimeStampWithNull(chatMessage.getCreatedAt()))
                .writerNickname(chatMessage.isSentByShop() ? chatMessage.getShop().getShopName() : chatMessage.getMember().getNickname())
                .sentByShop(chatMessage.isSentByShop())
                .build();
    }

    public static ChatMessageDto of(ChatMessage chatMessage, boolean isShopView) {
        ChatMessageDto dto = of(chatMessage);
        dto.setReadByMember(isShopView ? chatMessage.isReadByShop() : chatMessage.isReadByMember());
        return dto;
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