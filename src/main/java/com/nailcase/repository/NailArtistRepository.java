package com.nailcase.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.SocialType;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface NailArtistRepository extends JpaRepository<NailArtist, Long> {
	Optional<NailArtist> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<NailArtist> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

	@Query("SELECT na FROM NailArtist na LEFT JOIN FETCH na.shops WHERE na.socialType = :socialType AND na.socialId = :socialId")
	Optional<NailArtist> findBySocialTypeAndSocialIdWithShops(@Param("socialType") SocialType socialType,
		@Param("socialId") String socialId);

	List<NailArtist> findByShopsShopId(Long shopId);
}