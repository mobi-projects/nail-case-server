package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Shop;

public interface ShopQuerydslRepository {
	Page<Shop> searchShop(String param, Pageable pageable);

	Page<Shop> findTopShopsByPopularityCriteria(Pageable pageable);

	Page<Shop> findLikedShopsByMember(Long memberId, Pageable pageable);

	Optional<Shop> findByShopIdAndNailArtistsAndWorkHours(Long shopId);

	double calculateShopReviewRating(Long shopId);

}