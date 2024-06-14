package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.Shop;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
}
