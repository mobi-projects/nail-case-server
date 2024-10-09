package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.dto.ShopDto;
import com.nailcase.model.entity.Shop;

public interface ShopQuerydslRepository {
	Page<Shop> searchShop(String param, Pageable pageable);

	Page<ShopDto.MainPageBeforeResponse> getTopPopularShops(Optional<Long> memberId, Pageable pageable);

	Optional<Shop> findByShopIdAndNailArtistsAndWorkHours(Long shopId);

	double calculateShopReviewRating(Long shopId);

	Optional<Shop> findByShopIdWithNailArtist(Long shopId);

	boolean isShopLikedByMember(Long shopId, Long memberId);
}