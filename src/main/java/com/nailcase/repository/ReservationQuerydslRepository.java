package com.nailcase.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Reservation;
import com.nailcase.model.enums.ReservationStatus;

public interface ReservationQuerydslRepository {
	public Page<Reservation> findReservationListWithinDateRange(Long shopId, LocalDateTime startDate,
		LocalDateTime endDate, ReservationStatus status, Pageable pageable);

	List<Reservation> fetchUpcomingReservationWithReservationDetails(Long memberId, Pageable pageable);

	List<Reservation> fetchCompletedReservationsWithDetailAndShop(Long memberId, Pageable pageable);
}
