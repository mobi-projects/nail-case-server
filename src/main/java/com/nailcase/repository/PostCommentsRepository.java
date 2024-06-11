package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.domain.post.comment.PostComment;

public interface PostCommentsRepository extends JpaRepository<PostComment, Long> {
}

