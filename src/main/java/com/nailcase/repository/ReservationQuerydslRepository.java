package com.nailcase.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Reservation;

public interface ReservationQuerydslRepository {
	List<Reservation> findReservationListWithinDateRange(Long shopId, LocalDateTime startDate, LocalDateTime endDate);

	List<Reservation> fetchUpcomingReservationWithReservationDetails(Long memberId, Pageable pageable);

	List<Reservation> fetchCompletedReservationDetailsWithMemberAndShop(Long memberId, Pageable pageable);
}
