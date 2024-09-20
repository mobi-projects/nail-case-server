package com.nailcase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.SocialType;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<Member> findByMemberIdAndRole(Long memberId, Role role);

	Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

}