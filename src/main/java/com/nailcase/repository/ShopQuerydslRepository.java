package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Shop;
import com.querydsl.core.Tuple;

public interface ShopQuerydslRepository {
	Page<Shop> searchShop(String param, Pageable pageable);

	Page<Tuple> getTopPopularShops(Optional<Long> memberId, Pageable pageable);

	Optional<Shop> findByShopIdAndNailArtistsAndWorkHours(Long shopId);

	double calculateShopReviewRating(Long shopId);

}