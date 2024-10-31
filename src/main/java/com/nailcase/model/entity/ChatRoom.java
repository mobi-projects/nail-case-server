package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.ChatRoomStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "chat_rooms")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus chatRoomStatus = ChatRoomStatus.ACTIVE;

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    public ChatMessage getLastMessage() {
        if (messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }


    public long getUnreadMessageCountForMember(Long memberId) {
        return messages.stream()
                .filter(message -> !message.isReadByMember() &&
                        !message.getMember().getMemberId().equals(memberId))
                .count();
    }


    public long getUnreadMessageCountForShop() {
        return messages.stream()
                .filter(message -> !message.isReadByShop() &&
                        message.getMember() != null)
                .count();
    }

    public void markMessagesAsReadByMember(Long memberId) {
        messages.stream()
                .filter(message -> !message.isReadByMember() &&
                        !message.getMember().getMemberId().equals(memberId))
                .forEach(ChatMessage::markAsReadByMember);
    }

    public void markMessagesAsReadByShop() {
        messages.stream()
                .filter(message -> !message.isReadByShop())
                .forEach(ChatMessage::markAsReadByShop);
    }
}