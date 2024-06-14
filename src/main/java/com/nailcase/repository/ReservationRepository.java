package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
