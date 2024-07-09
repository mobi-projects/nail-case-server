package com.nailcase.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;

public interface ShopQuerydslRepository {
	Page<Shop> searchShop(String param, Pageable pageable);

	Page<Shop> findTopShopsByPopularityCriteria(Pageable pageable);

	Page<Shop> findLikedShopsByMember(Member member, Pageable pageable);

	Page<Shop> findShopsByIds(List<Long> ids, Pageable pageable);

}