package com.nailcase.repository;

import java.util.Optional;

import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.SocialType;

public interface NailArtistQuerydslRepository {
	Optional<NailArtist> findByIdWithShops(Long nailArtistId);

	Optional<NailArtist> findBySocialTypeAndSocialIdWithShops(SocialType socialType, String socialId);
}
