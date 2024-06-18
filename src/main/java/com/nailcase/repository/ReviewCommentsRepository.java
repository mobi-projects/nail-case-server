package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.ReviewComment;

public interface ReviewCommentsRepository extends JpaRepository<ReviewComment, Long> {
}
