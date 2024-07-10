package com.nailcase.oauth.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Shop;
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

		List<Long> shopIds = nailArtist.getShops().stream()
			.map(Shop::getShopId)
			.collect(Collectors.toList());

		boolean hasShop = !shopIds.isEmpty();

		return LoginResponseDto.builder()
			.accessToken(accessTokenJwt)
			.refreshToken(refreshToken)
			.shopIds(shopIds)
			.hasShop(hasShop)
			.userType(UserType.MANAGER)  // 네일 아티스트(매니저) 타입 지정
			.build();
	}

	private NailArtist getOrCreateManager(OAuthAttributes attributes, SocialType socialType) {
		return nailArtistRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
			.orElseGet(
				() -> nailArtistRepository.save(
					attributes.toManagerEntity(socialType, attributes.getOauth2UserInfo())));
	}
}