package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.MonthlyArtLikedMember;

public interface MonthlyArtLikedMemberRepository extends JpaRepository<MonthlyArtLikedMember, Long> {

	Boolean existsByMonthlyArt_MonthlyArtIdAndMember_MemberId(Long monthlyId, Long memberId);

	Optional<MonthlyArtLikedMember> findByMonthlyArt_MonthlyArtAndMember_MemberId(Long monthlyId, Long memberId);

	List<MonthlyArtLikedMember> findByMonthlyArt_MonthlyArtInAndMember_MemberId(List<Long> monthlyIds, Long memberId);

}