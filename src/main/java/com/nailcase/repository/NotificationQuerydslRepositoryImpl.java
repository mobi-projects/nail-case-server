package com.nailcase.repository;

import java.util.List;

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
	public List<Notification> findByMemberReceiverId(Long memberId) {
		QNotification notification = QNotification.notification;

		// 멤버가 받는 알림 조건 설정
		Predicate condition = notification.receiverId.eq(memberId)
			.and(notification.notificationType.in(NotificationType.RESERVATION_APPROVE,
				NotificationType.RESERVATION_REJECT));

		// 쿼리 실행

		// 페이징 처리
		return queryFactory.selectFrom(notification)
			.where(condition)
			.leftJoin(notification.reservationDetail).fetchJoin()
			.limit(15)
			.orderBy(notification.createdAt.desc())
			.fetch();
	}

	@Override
	public List<Notification> findByNailArtistReceiverId(Long nailArtistId) {
		QNotification notification = QNotification.notification;

		// 네일 아티스트가 받는 알림 조건 설정
		Predicate condition = notification.receiverId.eq(nailArtistId)
			.and(notification.notificationType.in(NotificationType.RESERVATION_REQUEST,
				NotificationType.RESERVATION_CANCEL));

		// 페이징 처리
		return queryFactory.selectFrom(notification)
			.where(condition)
			.leftJoin(notification.reservationDetail).fetchJoin()
			.limit(15)
			.orderBy(notification.createdAt.desc())
			.fetch();
	}

	@Override
	public List<Notification> findByTypeAndReceiverIdWithNotRead(Long receiverId, Role role) {
		QNotification notification = QNotification.notification;
		BooleanExpression condition = role == Role.MANAGER ?
			notification.notificationType.in(NotificationType.RESERVATION_REQUEST,
				NotificationType.RESERVATION_CANCEL) :
			notification.notificationType.in(NotificationType.RESERVATION_APPROVE, NotificationType.RESERVATION_REJECT);

		return queryFactory.selectFrom(notification)
			.where(notification.receiverId.eq(receiverId)
					.and(condition),
				notification.isRead.eq(false)
			)
			.join(notification.reservationDetail).fetchJoin()
			.orderBy(notification.createdAt.desc())
			.fetch();
	}

	@Override
	public void updateReadStatusInNotReadNotification() {
		QNotification notification = QNotification.notification;
		queryFactory.update(notification)
			.set(notification.isRead, true)
			.where(notification.isRead.eq(false))
			.execute();
	}

}

