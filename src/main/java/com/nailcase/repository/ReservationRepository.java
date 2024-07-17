package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nailcase.model.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationQuerydslRepository {
	@Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.reservationDetailList WHERE r.reservationId = :reservationId")
	Optional<Reservation> findByIdWithReservationDetail(Long reservationId);
}
