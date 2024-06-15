package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ShopInfo;

@Repository
public interface ShopInfoRepository extends JpaRepository<ShopInfo, Long> {
}
