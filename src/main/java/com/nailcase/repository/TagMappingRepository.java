package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.TagMapping;

public interface TagMappingRepository extends JpaRepository<TagMapping, Long> {
}
