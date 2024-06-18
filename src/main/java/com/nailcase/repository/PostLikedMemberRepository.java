package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.PostLikedMember;

public interface PostLikedMemberRepository extends JpaRepository<PostLikedMember, Long> {

	Boolean existsByPost_PostIdAndMember_MemberId(Long postId, Long memberId);

	Optional<PostLikedMember> findByPost_PostIdAndMember_MemberId(Long postId, Long memberId);

	List<PostLikedMember> findByPost_PostIdInAndMember_MemberId(List<Long> postIds, Long memberId);

}
