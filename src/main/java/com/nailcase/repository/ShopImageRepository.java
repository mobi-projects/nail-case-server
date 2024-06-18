package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.ShopImage;

public interface ShopImageRepository extends JpaRepository<ShopImage, Long> {
}
