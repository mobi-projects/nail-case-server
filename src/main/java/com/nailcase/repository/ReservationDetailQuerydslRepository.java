package com.nailcase.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.nailcase.model.entity.ReservationDetail;
import com.nailcase.model.enums.ReservationStatus;

public interface ReservationDetailQuerydslRepository {
	List<ReservationDetail> findOngoingReservationDetailList(Long shopId, LocalDateTime startTime);

	List<ReservationDetail> findReservationByShopIdAndOnDate(Long shopId, LocalDateTime time);

	Integer countVisitsByMemberAndShop(Long memberId, Long shopId, LocalDateTime commentDate);

	void updateReservationDetailStatus(Long reservationDetailId, ReservationStatus status);
}
