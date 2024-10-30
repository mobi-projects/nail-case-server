package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "chat_messages")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String message;


    @Builder.Default
    @Column(name = "is_read_by_member")
    private boolean readByMember = false;

    @Builder.Default
    @Column(name = "is_read_by_shop")
    private boolean readByShop = false;

    @Builder.Default
    @Column(name = "is_sent_by_shop")
    private boolean sentByShop = false;  // 메시지 발신자 타입


    public void markAsReadByMember() {
        this.readByMember = true;
    }

    public void markAsReadByShop() {
        this.readByShop = true;
    }

    public boolean isUnread() {
        return sentByShop ? !readByMember : !readByShop;
    }

}