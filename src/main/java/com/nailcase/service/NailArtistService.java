package com.nailcase.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.model.entity.NailArtist;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NailArtistService {

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
