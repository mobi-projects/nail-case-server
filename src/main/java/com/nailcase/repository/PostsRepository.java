package com.nailcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.Post;

public interface PostsRepository extends JpaRepository<Post, Long> {
	List<Post> findByShop_ShopId(Long shopId);
}
