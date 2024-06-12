package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.review.Review;

public interface ReviewsRepository extends JpaRepository<Review, Long> {
	List<Review> findByShopId(Long shopId);

	Optional<Review> findByShopIdAndReviewId(Long shopId, Long reviewId);
}