package com.nailcase.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.NailArtistErrorCode;
import com.nailcase.model.entity.NailArtist;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NailArtistService {
	public void verifyArtistsExistenceInShop(List<Long> nailArtistIds, Collection<NailArtist> nailArtists) {
		if (notContainsAnyArtistIds(nailArtistIds, nailArtists)) {
			throw new BusinessException(NailArtistErrorCode.NOT_FOUND);
		}
	}

	private boolean notContainsAnyArtistIds(List<Long> nailArtistIds, Collection<NailArtist> nailArtists) {
		if (nailArtistIds == null || nailArtistIds.isEmpty()) {
			return false;
		}
		return nailArtistIds.stream()
			.noneMatch(artistId -> nailArtists
				.stream()
				.map(NailArtist::getNailArtistId)
				.collect(Collectors.toSet())
				.contains(artistId));
	}
}
