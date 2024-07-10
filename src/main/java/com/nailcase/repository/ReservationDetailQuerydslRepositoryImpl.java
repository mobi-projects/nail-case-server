package com.nailcase.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.QNailArtist;
import com.nailcase.model.entity.QReservationDetail;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.QWorkHour;
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

	@Override
	public List<ReservationDetail> findReservationByShopIdAndOnDate(Long shopId, LocalDateTime time) {
		LocalDateTime startOfDate = time.with(LocalTime.MIN);
		LocalDateTime endOfDate = time.with(LocalTime.MAX);

		return queryFactory.selectFrom(QReservationDetail.reservationDetail)
			.join(QReservationDetail.reservationDetail.shop, QShop.shop)
			.fetchJoin()
			.join(QShop.shop.workHours, QWorkHour.workHour)
			.fetchJoin()
			.leftJoin(QReservationDetail.reservationDetail.nailArtist, QNailArtist.nailArtist)
			.fetchJoin()
			.where(QShop.shop.shopId.eq(shopId),
				QReservationDetail.reservationDetail.startTime.between(startOfDate, endOfDate),
				QReservationDetail.reservationDetail.status.eq(ReservationStatus.CONFIRMED))
			.orderBy(QReservationDetail.reservationDetail.startTime.asc(),
				QReservationDetail.reservationDetail.endTime.asc())
			.fetch();
	}
}
