package com.nailcase.service;

import java.util.Arrays;
import java.util.Collection;
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
	public void verifyArtistsExistenceInShop(Long[] nailArtistIds, Collection<NailArtist> nailArtists) {
		if (notContainsAnyArtistIds(nailArtistIds, nailArtists)) {
			throw new BusinessException(NailArtistErrorCode.NOT_FOUND);
		}
	}

	private boolean notContainsAnyArtistIds(Long[] nailArtistIds, Collection<NailArtist> nailArtists) {
		return Arrays.stream(nailArtistIds)
			.noneMatch(artistId -> nailArtists
				.stream()
				.map(NailArtist::getNailArtistId)
				.collect(Collectors.toSet())
				.contains(artistId));
	}
}
