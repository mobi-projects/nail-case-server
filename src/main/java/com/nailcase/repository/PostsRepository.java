package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.Post;

public interface PostsRepository extends JpaRepository<Post, Long> {
	// List<Post> findByShopId(Long shopId);
}
