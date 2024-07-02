package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.Post;

public interface PostsRepository extends JpaRepository<Post, Long> {
	List<Post> findByShop_ShopId(Long shopId);

	Optional<Post> findByPostId(Long postId);

	Optional<Post> findByShop_ShopIdAndPostId(Long shopId, Long postId);

	Optional<Post> findPostWithImagesByPostId(Long postId);
}
