package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
	Optional<PostImage> findByImageId(Long imageId);

	Optional<PostImage> findByObjectName(String objectName);

}