package com.nailcase.repository;

import static com.nailcase.model.entity.QCondition.*;
import static com.nailcase.model.entity.QMember.*;
import static com.nailcase.model.entity.QNailArtist.*;
import static com.nailcase.model.entity.QReservation.*;
import static com.nailcase.model.entity.QReservationDetail.*;
import static com.nailcase.model.entity.QShop.*;
import static com.nailcase.model.entity.QTreatment.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.enums.ReservationStatus;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationQuerydslRepositoryImpl implements ReservationQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Reservation> findReservationListWithinDateRange(Long shopId, LocalDateTime startDate,
		LocalDateTime endDate, ReservationStatus status) {
		List<Reservation> reservationList = queryFactory.selectFrom(reservation)
			.leftJoin(reservation.reservationDetailList, reservationDetail)
			.fetchJoin()
			.leftJoin(reservation.shop, shop)
			.fetchJoin()
			.leftJoin(reservationDetail.nailArtist, nailArtist)
			.fetchJoin()
			.where(reservation.shop.shopId.eq(shopId),
				reservationDetail.startTime.between(startDate, endDate),
				reservationDetail.status.eq(status))
			.orderBy(reservationDetail.startTime.asc())
			.fetch();

		List<Long> reservationDetailIdList = reservationList.stream()
			.map(Reservation::getReservationDetailList)
			.flatMap(Collection::stream)
			.map(ReservationDetail::getReservationDetailId)
			.toList();

		queryFactory.selectFrom(condition)
			.where(condition.reservationDetail.reservationDetailId.in(reservationDetailIdList))
			.fetch();

		queryFactory.selectFrom(treatment)
			.where(treatment.reservationDetail.reservationDetailId.in(reservationDetailIdList))
			.fetch();

		return reservationList;
	}

	// 아직 시술 받기전 예약들
	public List<Reservation> fetchUpcomingReservationWithReservationDetails(Long memberId, Pageable pageable) {
		// 먼저 조건에 맞는 Reservation ID들과 해당하는 가장 빠른 startTime을 가져옵니다.
		List<Tuple> reservationIdsWithStartTime = queryFactory
			.select(reservation.reservationId, reservationDetail.startTime.min())
			.from(reservation)
			.join(reservation.reservationDetailList, reservationDetail)
			.where(reservation.customer.memberId.eq(memberId)
				.and(reservationDetail.startTime.after(LocalDateTime.now()))
				.and(reservationDetail.status.in(ReservationStatus.PENDING, ReservationStatus.CONFIRMED))
			)
			.groupBy(reservation.reservationId)
			.orderBy(reservationDetail.startTime.min().asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		List<Long> reservationIds = reservationIdsWithStartTime.stream()
			.map(tuple -> tuple.get(reservation.reservationId))
			.collect(Collectors.toList());

		// 그 다음 가져온 ID들을 사용하여 필요한 데이터를 한 번에 조회합니다.
		return queryFactory
			.selectFrom(reservation)
			.leftJoin(reservation.customer, member).fetchJoin()
			.leftJoin(reservation.shop, shop).fetchJoin()
			.leftJoin(shop.nailArtist).fetchJoin()
			.leftJoin(reservation.nailArtist).fetchJoin()
			.leftJoin(reservation.reservationDetailList, reservationDetail).fetchJoin()
			.leftJoin(reservationDetail.treatmentList).fetchJoin()
			.leftJoin(reservationDetail.conditionList).fetchJoin()
			.where(reservation.reservationId.in(reservationIds))
			.orderBy(reservation.reservationId.asc())
			.distinct()
			.fetch();
	}

	// 시술 받은 후 예약들
	@Override
	public List<Reservation> fetchCompletedReservationDetailsWithMemberAndShop(Long memberId, Pageable pageable) {
		List<Long> reservationIds = queryFactory
			.select(reservation.reservationId)
			.from(reservation)
			.join(reservation.reservationDetailList, reservationDetail)
			.where(reservation.customer.memberId.eq(memberId)
				.and(reservationDetail.startTime.before(LocalDateTime.now()))
				.and(reservationDetail.status.eq(ReservationStatus.CONFIRMED))
			)
			.orderBy(reservationDetail.startTime.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return queryFactory
			.selectFrom(reservation)
			.leftJoin(reservation.customer, member).fetchJoin()
			.leftJoin(reservation.shop, shop).fetchJoin()
			.leftJoin(shop.nailArtist).fetchJoin()
			.leftJoin(reservation.nailArtist).fetchJoin()
			.leftJoin(reservation.reservationDetailList, reservationDetail).fetchJoin()
			.where(reservation.reservationId.in(reservationIds))
			.orderBy(reservationDetail.startTime.desc())
			.fetch();
	}
}
