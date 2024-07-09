package com.nailcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationQuerydslRepository {
	List<Reservation> findByCustomer(Member member);  // 회원별 예약 정보 조회

}
