package com.nailcase.oauth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.SocialType;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.OAuthAttributes;
import com.nailcase.repository.NailArtistRepository;

import jakarta.transaction.Transactional;

@Service("kakaoManagerLoginService")
public class KakaoManagerLoginService extends AbstractKakaoLoginService {

	private final NailArtistRepository nailArtistRepository;
	private final JwtService jwtService;

	public KakaoManagerLoginService(RestTemplate restTemplate, JwtService jwtService,
		NailArtistRepository nailArtistRepository) {
		super(restTemplate, jwtService);
		this.nailArtistRepository = nailArtistRepository;
		this.jwtService = jwtService;
	}

	@Transactional
	@Override
	protected LoginResponseDto processUserLogin(OAuthAttributes attributes) {
		NailArtist nailArtist = getOrCreateManager(attributes);
		String accessTokenJwt = jwtService.createAccessToken(nailArtist.getEmail(), nailArtist.getNailArtistId(),
			nailArtist.getRole());
		String refreshToken = jwtService.createRefreshToken(nailArtist.getEmail(), nailArtist.getNailArtistId(),
			nailArtist.getRole());
		jwtService.updateRefreshToken(nailArtist.getEmail(), refreshToken, nailArtist.getRole());

		List<Long> shopIds = nailArtist.getShops().stream()
			.map(Shop::getShopId)
			.collect(Collectors.toList());

		boolean hasShop = !shopIds.isEmpty();

		return LoginResponseDto.builder()
			.accessToken(accessTokenJwt)
			.refreshToken(refreshToken)
			.shopIds(shopIds)
			.hasShop(hasShop)
			.role(nailArtist.getRole())
			.profileImageUrl(nailArtist.getProfileImageUrl())
			.build();
	}

	private NailArtist getOrCreateManager(OAuthAttributes attributes) {
		return nailArtistRepository.findBySocialTypeAndSocialIdWithShops(
				SocialType.KAKAO, attributes.getOauth2UserInfo().getId())
			.orElseGet(
				() -> nailArtistRepository.save(
					attributes.toManagerEntity(SocialType.KAKAO, attributes.getOauth2UserInfo())));
	}
}