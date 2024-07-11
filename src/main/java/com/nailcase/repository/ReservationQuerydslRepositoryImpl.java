package com.nailcase.repository;

import static com.nailcase.model.entity.QMember.*;
import static com.nailcase.model.entity.QNailArtist.*;
import static com.nailcase.model.entity.QReservation.*;
import static com.nailcase.model.entity.QShop.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.QCondition;
import com.nailcase.model.entity.QReservationDetail;
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
		List<Reservation> reservationList = queryFactory.selectFrom(reservation)
			.leftJoin(reservation.reservationDetailList, QReservationDetail.reservationDetail)
			.fetchJoin()
			.leftJoin(reservation.shop, shop)
			.fetchJoin()
			.leftJoin(reservation.nailArtist, nailArtist)
			.fetchJoin()
			.where(reservation.shop.shopId.eq(shopId),
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

	public List<Reservation> fetchReservationsWithMemberAndShop(Long memberId) {
		return queryFactory
			.selectFrom(reservation)
			.leftJoin(reservation.customer, member).fetchJoin()
			.leftJoin(reservation.shop, shop).fetchJoin()
			.leftJoin(shop.nailArtist).fetchJoin()
			.leftJoin(reservation.nailArtist).fetchJoin()
			.where(reservation.customer.memberId.eq(memberId))
			.orderBy(reservation.createdAt.desc())
			.fetch();
	}
}
