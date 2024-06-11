package com.nailcase.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.domain.post.Post;

public interface PostsRepository extends JpaRepository<Post, Long> {
	List<Post> findByShopId(Long shopId);
}