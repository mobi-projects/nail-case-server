package com.nailcase.repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.QNailArtist;
import com.nailcase.model.entity.QReservation;
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
	public List<ReservationDetail> findOngoingReservationDetailList(
		Long shopId,
		LocalDateTime startTime
	) {
		return queryFactory.selectFrom(QReservationDetail.reservationDetail)
			.join(QReservationDetail.reservationDetail.shop, QShop.shop)
			.fetchJoin()
			.where(QReservationDetail.reservationDetail.shop.shopId.eq(shopId),
				QReservationDetail.reservationDetail.startTime.eq(startTime),
				QReservationDetail.reservationDetail.status.eq(ReservationStatus.PENDING),
				QReservationDetail.reservationDetail.status.eq(ReservationStatus.CONFIRMED))
			.orderBy(QReservationDetail.reservationDetail.startTime.asc())
			.fetch();
	}

	@Override
	public List<ReservationDetail> findReservationByShopIdAndOnDate(Long shopId, LocalDateTime time) {
		LocalDateTime startOfDate = time.with(LocalTime.MIN);
		LocalDateTime endOfDate = time.with(LocalTime.MAX);

		return queryFactory.selectFrom(QReservationDetail.reservationDetail)
			.leftJoin(QReservationDetail.reservationDetail.nailArtist, QNailArtist.nailArtist)
			.fetchJoin()
			.where(QReservationDetail.reservationDetail.shop.shopId.eq(shopId),
				QReservationDetail.reservationDetail.startTime.between(startOfDate, endOfDate),
				QReservationDetail.reservationDetail.status.eq(ReservationStatus.CONFIRMED))
			.orderBy(QReservationDetail.reservationDetail.startTime.asc(),
				QReservationDetail.reservationDetail.endTime.asc())
			.fetch();
	}

	@Override
	public Integer countVisitsByMemberAndShop(Long memberId, Long shopId, LocalDateTime reservationDate) {
		QReservation reservation = QReservation.reservation;
		QShop shop = QShop.shop;

		return Math.toIntExact(queryFactory
			.selectDistinct(reservation.reservationId)
			.from(reservation)
			.join(reservation.shop, shop)
			.where(
				reservation.customer.memberId.eq(memberId),
				shop.shopId.eq(shopId),
				reservation.reservationDetail.status.eq(ReservationStatus.CONFIRMED),
				reservation.reservationDetail.startTime.loe(reservationDate)
			)
			.fetch().size());
	}

}
