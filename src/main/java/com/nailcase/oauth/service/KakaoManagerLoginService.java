package com.nailcase.oauth.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.SocialType;
import com.nailcase.model.enums.UserType;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.OAuthAttributes;
import com.nailcase.repository.NailArtistRepository;

@Service("kakaoManagerLoginService")
public class KakaoManagerLoginService extends AbstractKakaoLoginService {

	private final NailArtistRepository nailArtistRepository;

	public KakaoManagerLoginService(RestTemplate restTemplate, JwtService jwtService,
		NailArtistRepository nailArtistRepository) {
		super(restTemplate, jwtService);
		this.nailArtistRepository = nailArtistRepository;
	}

	@Override
	protected LoginResponseDto processUserLogin(OAuthAttributes attributes) {
		NailArtist nailArtist = getOrCreateManager(attributes, SocialType.KAKAO);
		String accessTokenJwt = jwtService.createAccessToken(nailArtist.getEmail(), nailArtist.getNailArtistId(),
			UserType.MANAGER.getValue());
		String refreshToken = jwtService.createRefreshToken(nailArtist.getEmail(), UserType.MANAGER.getValue());
		jwtService.updateRefreshToken(nailArtist.getEmail(), refreshToken, UserType.MANAGER.getValue());

		return LoginResponseDto.builder()
			.accessToken(accessTokenJwt)
			.refreshToken(refreshToken)
			.build();
	}

	private NailArtist getOrCreateManager(OAuthAttributes attributes, SocialType socialType) {
		return nailArtistRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
			.orElseGet(
				() -> nailArtistRepository.save(
					attributes.toManagerEntity(socialType, attributes.getOauth2UserInfo())));
	}
}