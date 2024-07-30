package com.nailcase.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.ShopLikedMember;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ShopLikedMemberRepository extends JpaRepository<ShopLikedMember, Long> {
	Boolean existsByShop_ShopIdAndMember_MemberId(Long shopId, Long memberId);

	Optional<ShopLikedMember> findByShop_ShopIdAndMember_MemberId(Long shopId, Long memberId);

	@Query("SELECT s.shop.shopId FROM ShopLikedMember s WHERE s.member.memberId = :memberId")
	Set<Long> findLikedShopIdsByMemberId(@Param("memberId") Long memberId);

}