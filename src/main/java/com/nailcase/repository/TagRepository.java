package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
