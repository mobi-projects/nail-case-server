package com.nailcase.repository;

import static com.nailcase.model.entity.QCondition.*;
import static com.nailcase.model.entity.QNailArtist.*;
import static com.nailcase.model.entity.QReservation.*;
import static com.nailcase.model.entity.QReservationDetail.*;
import static com.nailcase.model.entity.QShop.*;
import static com.nailcase.model.entity.QTreatment.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.QMember;
import com.nailcase.model.entity.QReservation;
import com.nailcase.model.entity.QReservationDetail;
import com.nailcase.model.entity.QShop;
import com.nailcase.model.entity.QShopImage;
import com.nailcase.model.entity.Reservation;
import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.enums.ReservationStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationQuerydslRepositoryImpl implements ReservationQuerydslRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Reservation> findReservationListWithinDateRange(Long shopId, LocalDateTime startDate,
		LocalDateTime endDate, ReservationStatus status, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();

		// shopId 조건은 항상 적용
		builder.and(reservation.shop.shopId.eq(shopId));

		// startDate와 endDate가 모두 제공된 경우에만 날짜 범위 조건 적용
		if (startDate != null && endDate != null) {
			builder.and(reservationDetail.startTime.between(startDate, endDate));
		}

		// status가 제공된 경우에만 상태 조건 적용
		if (status != null) {
			builder.and(reservationDetail.status.eq(status));
		}

		// 전체 카운트 쿼리
		long total = queryFactory
			.selectFrom(reservation)
			.where(builder)
			.fetchCount();

		// 페이지네이션이 적용된 쿼리
		List<Reservation> reservationList = queryFactory
			.selectFrom(reservation)
			.leftJoin(reservation.reservationDetail, reservationDetail)
			.fetchJoin()
			.leftJoin(reservation.shop, shop)
			.fetchJoin()
			.leftJoin(reservationDetail.nailArtist, nailArtist)
			.fetchJoin()
			.leftJoin(reservationDetail.treatment, treatment)
			.fetchJoin()
			.where(builder)
			.orderBy(reservationDetail.startTime.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 연관된 조건 데이터 조회
		if (!reservationList.isEmpty()) {
			List<Long> reservationDetailIdList = reservationList.stream()
				.map(Reservation::getReservationDetail)
				.map(ReservationDetail::getReservationDetailId)
				.toList();

			queryFactory.selectFrom(condition)
				.where(condition.reservationDetail.reservationDetailId.in(reservationDetailIdList))
				.fetch();
		}

		return new PageImpl<>(reservationList, pageable, total);
	}

	// 아직 시술 받기전 예약들
	public List<Reservation> fetchUpcomingReservationWithReservationDetails(Long memberId, Pageable pageable) {
		QReservation reservation = QReservation.reservation;
		QReservationDetail reservationDetail = QReservationDetail.reservationDetail;
		QMember member = QMember.member;
		QShop shop = QShop.shop;
		QShopImage shopImage = QShopImage.shopImage;

		// 먼저 조건에 맞는 Reservation ID들과 해당하는 가장 빠른 startTime을 가져옵니다.
		List<Tuple> reservationIdsWithStartTime = queryFactory
			.select(reservation.reservationId, reservationDetail.startTime.min())
			.from(reservation)
			.join(reservation.reservationDetail, reservationDetail)
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
			.distinct()
			.leftJoin(reservation.customer, member).fetchJoin()
			.leftJoin(reservation.shop, shop).fetchJoin()
			.leftJoin(shop.nailArtist).fetchJoin()
			.leftJoin(reservation.nailArtist).fetchJoin()
			.leftJoin(reservation.reservationDetail, reservationDetail).fetchJoin()
			.leftJoin(reservationDetail.treatment).fetchJoin()
			.leftJoin(reservationDetail.conditionList).fetchJoin()
			.leftJoin(shop.shopImages, shopImage)
			.on(shopImage.imageId.eq(
				JPAExpressions
					.select(shopImage.imageId.min())
					.from(shopImage)
					.where(shopImage.shop.eq(shop))
			))
			.where(reservation.reservationId.in(reservationIds))
			.orderBy(reservation.reservationId.asc())
			.fetch();
	}

	// 시술 받은 후 예약들
	@Override
	public List<Reservation> fetchCompletedReservationsWithDetailAndShop(Long memberId, Pageable pageable) {
		return queryFactory
			.selectFrom(reservation)
			.join(reservation.reservationDetail, reservationDetail).fetchJoin()
			.join(reservation.shop, shop).fetchJoin()
			.leftJoin(reservation.nailArtist, nailArtist).fetchJoin()
			.where(reservation.customer.memberId.eq(memberId)
				.and(reservationDetail.startTime.before(LocalDateTime.now()))
				.and(reservationDetail.status.eq(ReservationStatus.CONFIRMED))
			)
			.orderBy(reservationDetail.startTime.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}
}
