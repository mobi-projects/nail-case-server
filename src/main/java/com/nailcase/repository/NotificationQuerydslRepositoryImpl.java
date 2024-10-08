package com.nailcase.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.Notification;
import com.nailcase.model.entity.QNotification;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.model.enums.Role;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationQuerydslRepositoryImpl implements NotificationQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Notification> findByMemberReceiverId(Long memberId, Pageable pageable) {
		QNotification notification = QNotification.notification;

		// 멤버가 받는 알림 조건 설정
		Predicate condition = notification.receiverId.eq(memberId)
			.and(notification.notificationType.in(NotificationType.RESERVATION_APPROVE,
				NotificationType.RESERVATION_REJECT));

		// 쿼리 실행
		List<Notification> results = queryFactory.selectFrom(notification)
			.where(condition)
			.leftJoin(notification.reservationDetail).fetchJoin()
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 페이징 처리
		return PageableExecutionUtils.getPage(results, pageable,
			() -> queryFactory.selectFrom(notification).where(condition).fetchCount());
	}

	@Override
	public Page<Notification> findByNailArtistReceiverId(Long nailArtistId, Pageable pageable) {
		QNotification notification = QNotification.notification;

		// 네일 아티스트가 받는 알림 조건 설정
		Predicate condition = notification.receiverId.eq(nailArtistId)
			.and(notification.notificationType.in(NotificationType.RESERVATION_REQUEST,
				NotificationType.RESERVATION_CANCEL));

		// 쿼리 실행
		List<Notification> results = queryFactory.selectFrom(notification)
			.where(condition)
			.leftJoin(notification.reservationDetail).fetchJoin()
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 페이징 처리
		return PageableExecutionUtils.getPage(results, pageable,
			() -> queryFactory.selectFrom(notification).where(condition).fetchCount());
	}

	@Override
	public Notification findByTypeAndReceiverIdWithNotSent(Long receiverId, Role role) {
		QNotification notification = QNotification.notification;
		BooleanExpression condition = role == Role.MANAGER ?
			notification.notificationType.in(NotificationType.RESERVATION_REQUEST,
				NotificationType.RESERVATION_CANCEL) :
			notification.notificationType.in(NotificationType.RESERVATION_APPROVE, NotificationType.RESERVATION_REJECT);

		return queryFactory.selectFrom(notification)
			.where(notification.receiverId.eq(receiverId)
					.and(condition),
				notification.isSent.eq(false)
				, notification.isRead.eq(false)
			)
			.leftJoin(notification.reservationDetail).fetchJoin()
			.orderBy(notification.createdAt.desc())
			.fetchFirst();
	}

}

