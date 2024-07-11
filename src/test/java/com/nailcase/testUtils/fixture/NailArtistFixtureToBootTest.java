package com.nailcase.testUtils.fixture;

import org.springframework.stereotype.Component;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.repository.NailArtistRepository;
import com.nailcase.testUtils.FixtureFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NailArtistFixtureToBootTest {

	private final JwtService jwtService;

	private final NailArtistRepository nailArtistRepository;

	public String createNailArtistAndGetJwt() {
		NailArtist nailArtist = FixtureFactory.nailArtistFixture.getNailArtist();

		nailArtistRepository.save(nailArtist);

		return jwtService.createAccessToken(nailArtist.getEmail(), nailArtist.getNailArtistId(),
			UserType.MANAGER.getValue());
	}

	public void deleteAllNailArtist() {
		nailArtistRepository.deleteAll();
	}

}
