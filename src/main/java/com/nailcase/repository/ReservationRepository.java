package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nailcase.model.entity.Reservation;

import io.lettuce.core.dynamic.annotation.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationQuerydslRepository {
	@Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.reservationDetail WHERE r.reservationId = :reservationId")
	Optional<Reservation> findByIdWithReservationDetail(Long reservationId);

	@Query("SELECT r FROM Reservation r " +
		"LEFT JOIN FETCH r.shop " +
		"LEFT JOIN FETCH r.customer " +
		"LEFT JOIN FETCH r.nailArtist " +
		"LEFT JOIN FETCH r.reservationDetail " +
		"WHERE r.reservationId = :id")
	Optional<Reservation> findReservationWithDetailsById(@Param("id") Long id);
}
