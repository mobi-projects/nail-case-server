package com.nailcase.repository;

import static com.nailcase.model.entity.QChatRoom.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatRoomQuerydslRepositoryImpl implements ChatRoomQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ChatRoom> findAllByChatRoomId(Long chatRoomId) {
		return queryFactory
			.selectFrom(chatRoom)
			.where(chatRoom.chatRoomId.eq(chatRoomId))
			.fetch();
	}

	@Override
	public Optional<ChatRoom> findByChatRoomId(Long chatRoomId) {
		return Optional.ofNullable(queryFactory
			.selectFrom(chatRoom)
			.where(chatRoom.chatRoomId.eq(chatRoomId))
			.fetchOne());
	}

	@Override
	public boolean existsByChatRoomId(Long chatRoomId) {
		return queryFactory
			.selectOne()
			.from(chatRoom)
			.where(chatRoom.chatRoomId.eq(chatRoomId))
			.fetchFirst() != null;
	}

}
