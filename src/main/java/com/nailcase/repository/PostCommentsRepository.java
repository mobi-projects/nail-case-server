package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.PostComment;

public interface PostCommentsRepository extends JpaRepository<PostComment, Long> {

	Optional<PostComment> findByCommentIdAndCreatedBy(Long commentId, Long createdBy);
}

