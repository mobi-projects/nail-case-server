package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.PostComment;

public interface PostCommentsRepository extends JpaRepository<PostComment, Long> {
}

