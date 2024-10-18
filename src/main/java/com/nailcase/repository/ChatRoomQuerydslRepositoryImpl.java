package com.nailcase.repository;

import static com.nailcase.model.entity.QChatRoom.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ChatRoom;
import com.nailcase.model.entity.QChatRoom;
import com.nailcase.model.entity.QShop;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatRoomQuerydslRepositoryImpl implements ChatRoomQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsByShopIdAndChatRoomId(Long shopId, Long chatRoomId) {
		return queryFactory
			.selectOne()
			.from(chatRoom)
			.where(
				chatRoom.shop.shopId.eq(shopId),
				chatRoom.chatRoomId.eq(chatRoomId))
			.fetchFirst() != null;
	}

	@Override
	public Optional<ChatRoom> findChatRoomByShopIdAndMemberId(Long shopId, Long memberId) {
		ChatRoom result = queryFactory
			.selectFrom(chatRoom)
			.where(
				chatRoom.shop.shopId.eq(shopId),
				chatRoom.member.memberId.eq(memberId)
			)
			.fetchFirst();
		return Optional.ofNullable(result);
	}

	@Override
	public Optional<ChatRoom> findChatRoomByShopIdAndChatRoomId(Long shopId, Long chatRoomId) {
		QChatRoom chatRoom = QChatRoom.chatRoom;
		QShop shop = QShop.shop;

		ChatRoom result = queryFactory
			.selectFrom(chatRoom)
			.leftJoin(chatRoom.shop, shop).fetchJoin()
			.where(
				shop.shopId.eq(shopId),
				chatRoom.chatRoomId.eq(chatRoomId)
			)
			.fetchFirst();

		return Optional.ofNullable(result);
	}
}
