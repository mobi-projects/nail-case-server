package com.nailcase.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.nailcase.model.entity.Reservation;

public interface ReservationQuerydslRepository {
	// List<Reservation> query(Long shopId, LocalDateTime startTime, LocalDateTime endTime);

	List<Reservation> findReservationListWithinDateRange(Long shopId, LocalDateTime startDate, LocalDateTime endDate);
}
