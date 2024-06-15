package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ShopHour;

@Repository
public interface ShopHourRepository extends JpaRepository<ShopHour, Long> {
}
