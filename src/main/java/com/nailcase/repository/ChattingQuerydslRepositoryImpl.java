package com.nailcase.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.QChatMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChattingQuerydslRepositoryImpl implements ChattingQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ChatMessage> findMessagesByChatRoomId(Long chatRoomId) {
		return queryFactory
			.selectFrom(QChatMessage.chatMessage)
			.where(QChatMessage.chatMessage.chatRoom.chatRoomId.eq(chatRoomId))
			.orderBy(QChatMessage.chatMessage.createdAt.asc())  // 시간순 정렬
			.fetch();
	}
}
