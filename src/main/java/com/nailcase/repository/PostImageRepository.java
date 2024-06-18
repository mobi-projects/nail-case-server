package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}