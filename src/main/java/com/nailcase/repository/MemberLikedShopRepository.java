package com.nailcase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.MemberLikedShop;

public interface MemberLikedShopRepository extends JpaRepository<MemberLikedShop, Long> {
	Page<MemberLikedShop> findByMember_MemberId(Long memberId, Pageable pageable);
}