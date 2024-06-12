package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.common.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}