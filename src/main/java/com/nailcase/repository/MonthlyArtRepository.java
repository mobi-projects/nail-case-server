package com.nailcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.MonthlyArt;

public interface MonthlyArtRepository extends JpaRepository<MonthlyArt, Long>, MonthlyArtQuerydslRepository {
	List<MonthlyArt> findByShop_ShopId(Long shopId);

}
