package com.nailcase.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.nailcase.model.entity.ReservationDetail;

public interface ReservationDetailQuerydslRepository {
	List<ReservationDetail> findOngoingReservationDetailList(Long shopId, LocalDateTime startTime,
		LocalDateTime endTime);
}
