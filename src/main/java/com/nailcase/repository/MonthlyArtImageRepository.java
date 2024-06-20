package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.MonthlyArtImage;

public interface MonthlyArtImageRepository extends JpaRepository<MonthlyArtImage, Long> {
	Optional<MonthlyArtImage> findByImageId(Long imageId);

}