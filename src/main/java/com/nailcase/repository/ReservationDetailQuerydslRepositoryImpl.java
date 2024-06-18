package com.nailcase.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.QReservationDetail;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.enums.ReservationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationDetailQuerydslRepositoryImpl implements ReservationDetailQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ReservationDetail> findOngoingReservationDetailList(Long shopId, LocalDateTime startTime,
		LocalDateTime endTime) {
		return queryFactory.selectFrom(QReservationDetail.reservationDetail)
			.join(QReservationDetail.reservationDetail.shop, QShop.shop)
			.fetchJoin()
			.where(QReservationDetail.reservationDetail.shop.shopId.eq(shopId),
				QReservationDetail.reservationDetail.status.ne(ReservationStatus.CANCELED),
				QReservationDetail.reservationDetail.status.ne(ReservationStatus.REJECTED),
				QReservationDetail.reservationDetail.startTime.before(endTime),
				QReservationDetail.reservationDetail.endTime.after(startTime))
			.orderBy(QReservationDetail.reservationDetail.startTime.asc())
			.fetch();
	}
}
