package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByShop_ShopId(Long shopId);
	
	Optional<Review> findByShop_ShopIdAndReviewId(Long shopId, Long reviewId);
}