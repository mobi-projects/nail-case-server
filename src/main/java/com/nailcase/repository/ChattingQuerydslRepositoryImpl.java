package com.nailcase.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
	public Page<ChatMessage> findByChatRoomIdWithPagination(Long chatRoomId, Pageable pageable) {
		QChatMessage chatMessage = QChatMessage.chatMessage;

		List<ChatMessage> messages = queryFactory.selectFrom(chatMessage)
			.where(chatMessage.chatRoom.chatRoomId.eq(chatRoomId))
			.orderBy(chatMessage.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory.selectFrom(chatMessage)
			.where(chatMessage.chatRoom.chatRoomId.eq(chatRoomId))
			.fetch().size();

		return new PageImpl<>(messages, pageable, total);
	}
}
