package com.nailcase.repository;

import java.util.Optional;

import com.nailcase.model.entity.MonthlyArt;

public interface MonthlyArtQuerydslRepository {
	Optional<MonthlyArt> findLatestByShop_ShopId(Long shopId);

}
