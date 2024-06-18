package com.nailcase.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.QCondition;
import com.nailcase.model.entity.QNailArtist;
import com.nailcase.model.entity.QReservation;
import com.nailcase.model.entity.QReservationDetail;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.QTreatment;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationQuerydslRepositoryImpl implements ReservationQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Reservation> findReservationListWithinDateRange(Long shopId, LocalDateTime startDate,
		LocalDateTime endDate) {
		List<Reservation> reservationList = queryFactory.selectFrom(QReservation.reservation)
			.leftJoin(QReservation.reservation.reservationDetailList, QReservationDetail.reservationDetail)
			.fetchJoin()
			.leftJoin(QReservation.reservation.shop, QShop.shop)
			.fetchJoin()
			.leftJoin(QReservation.reservation.nailArtist, QNailArtist.nailArtist)
			.fetchJoin()
			.where(QReservation.reservation.shop.shopId.eq(shopId),
				QReservationDetail.reservationDetail.startTime.between(startDate, endDate))
			.orderBy(QReservationDetail.reservationDetail.startTime.asc())
			.fetch();

		List<Long> reservationDetailIdList = reservationList.stream()
			.map(Reservation::getReservationDetailList)
			.flatMap(Collection::stream)
			.map(ReservationDetail::getReservationDetailId)
			.toList();

		queryFactory.selectFrom(QCondition.condition)
			.where(QCondition.condition.reservationDetail.reservationDetailId.in(reservationDetailIdList))
			.fetch();

		queryFactory.selectFrom(QTreatment.treatment)
			.where(QTreatment.treatment.reservationDetail.reservationDetailId.in(reservationDetailIdList))
			.fetch();

		return reservationList;
	}
}
