package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailcase.model.entity.MonthlyArtLikedMember;

public interface MonthlyArtLikedMemberRepository extends JpaRepository<MonthlyArtLikedMember, Long> {

	Boolean existsByMonthlyArt_MonthlyArtIdAndMember_MemberId(Long monthlyArtId, Long memberId);

	Optional<MonthlyArtLikedMember> findByMonthlyArt_MonthlyArtIdAndMember_MemberId(Long monthlyArtId, Long memberId);

	List<MonthlyArtLikedMember> findByMonthlyArt_MonthlyArtIdInAndMember_MemberId(List<Long> monthlyArtIds,
		Long memberId);

}
