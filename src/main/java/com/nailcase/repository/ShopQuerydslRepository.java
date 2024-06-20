package com.nailcase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Shop;

public interface ShopQuerydslRepository {
	Page<Shop> searchShop(String param, Pageable pageable);
}