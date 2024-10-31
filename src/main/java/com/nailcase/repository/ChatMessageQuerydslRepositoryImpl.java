package com.nailcase.repository;

import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.QChatMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatMessageQuerydslRepositoryImpl implements ChatMessageQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable) {
        QChatMessage chatMessage = QChatMessage.chatMessage;

        // 전체 카운트를 위한 쿼리
        Long totalCount = Optional.ofNullable(
                queryFactory
                        .select(chatMessage.count())
                        .from(chatMessage)
                        .where(chatRoomIdEq(chatRoomId))
                        .fetchOne()
        ).orElse(0L);

        // 실제 데이터를 가져오는 쿼리
        List<ChatMessage> messages = queryFactory
                .selectFrom(chatMessage)
                .join(chatMessage.chatRoom).fetchJoin()
                .join(chatMessage.member).fetchJoin()
                .join(chatMessage.shop).fetchJoin()
                .where(chatRoomIdEq(chatRoomId))
                .orderBy(chatMessage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(messages, pageable, totalCount);
    }

    private BooleanExpression chatRoomIdEq(Long chatRoomId) {
        return chatRoomId != null ? QChatMessage.chatMessage.chatRoom.chatRoomId.eq(chatRoomId) : null;
    }
}