package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ShopHours;

@Repository
public interface ShopHoursRepository extends JpaRepository<ShopHours, Long> {
}
