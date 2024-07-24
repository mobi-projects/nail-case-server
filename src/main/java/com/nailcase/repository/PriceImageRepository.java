package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.PriceImage;

public interface PriceImageRepository extends JpaRepository<PriceImage, Long> {
}
