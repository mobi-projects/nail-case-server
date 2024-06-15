package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.PostLikedMember;

public interface PostLikedMemberRepository extends JpaRepository<PostLikedMember, Long> {
	Boolean existsByPostIdAndMemberId(Long postId, Long memberId);

	Optional<PostLikedMember> findByPostIdAndMemberId(Long postId, Long memberId);

}
