package com.nailcase.repository;

import com.nailcase.model.entity.*;
import com.nailcase.model.enums.ChatRoomStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomQuerydslRepositoryImpl implements ChatRoomQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ChatRoom> findByShopAndMember(Shop shop, Member member) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatMessage message = QChatMessage.chatMessage;
        QShop qShop = QShop.shop;
        QMember qMember = QMember.member;

        return Optional.ofNullable(queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.messages, message).fetchJoin()
                .leftJoin(chatRoom.shop, qShop).fetchJoin()
                .leftJoin(chatRoom.member, qMember).fetchJoin()
                .where(chatRoom.shop.eq(shop)
                        .and(chatRoom.member.eq(member))
                        .and(chatRoom.chatRoomStatus.eq(ChatRoomStatus.ACTIVE)))
                .orderBy(chatRoom.createdAt.desc()
                        , message.createdAt.desc()  // 메시지도 생성일시 내림차순 정렬 추가
                ) // 생성일시 기준 정렬
                .fetchFirst()); // fetchOne() 대신 fetchFirst() 사용
    }

    @Override
    public List<ChatRoom> findByMemberIdAndChatRoomStatus(Long memberId, ChatRoomStatus status) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatMessage message = QChatMessage.chatMessage;
        QShop shop = QShop.shop;
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.messages, message).fetchJoin()
                .leftJoin(chatRoom.shop, shop).fetchJoin()
                .leftJoin(chatRoom.member, member).fetchJoin()
                .where(chatRoom.member.memberId.eq(memberId)
                        .and(chatRoom.chatRoomStatus.eq(status)))
                .orderBy(message.createdAt.desc())
                .fetch();
    }

    @Override
    public Optional<ChatRoom> findByChatRoomIdWithDetails(Long chatRoomId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatMessage message = QChatMessage.chatMessage;
        QShop shop = QShop.shop;
        QMember member = QMember.member;

        return Optional.ofNullable(queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.messages, message).fetchJoin()
                .leftJoin(chatRoom.shop, shop).fetchJoin()
                .leftJoin(chatRoom.member, member).fetchJoin()
                .where(chatRoom.chatRoomId.eq(chatRoomId))
                .orderBy(message.createdAt.desc())
                .fetchOne());
    }

    @Override
    public List<ChatRoom> findByShopIdAndChatRoomStatus(Long shopId, ChatRoomStatus status) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatMessage message = QChatMessage.chatMessage;
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.messages, message).fetchJoin()
                .leftJoin(chatRoom.member, member).fetchJoin()
                .where(chatRoom.shop.shopId.eq(shopId)
                        .and(chatRoom.chatRoomStatus.eq(status)))
                .orderBy(message.createdAt.desc())
                .fetch();
    }

}
