package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.ReservationDetail;

public interface ReservationDetailRepository
	extends JpaRepository<ReservationDetail, Long>, ReservationDetailQuerydslRepository {
}
