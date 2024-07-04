package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ShopInfo;

@Repository
public interface ShopInfoRepository extends JpaRepository<ShopInfo, Long> {
	Optional<ShopInfo> findByShopId(Long shopId);
}
