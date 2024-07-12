package com.nailcase.oauth.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.SocialType;
import com.nailcase.oauth.dto.LoginResponseDto;
import com.nailcase.oauth.dto.OAuthAttributes;
import com.nailcase.repository.MemberRepository;

@Service("kakaoMemberLoginService")
public class KakaoMemberLoginService extends AbstractKakaoLoginService {

	private final MemberRepository memberRepository;
	private final JwtService jwtService;

	public KakaoMemberLoginService(RestTemplate restTemplate, JwtService jwtService,
		MemberRepository memberRepository) {
		super(restTemplate, jwtService);
		this.memberRepository = memberRepository;
		this.jwtService = jwtService;
	}

	@Override
	protected LoginResponseDto processUserLogin(OAuthAttributes attributes) {
		Member member = getOrCreateMember(attributes);
		String accessTokenJwt = jwtService.createAccessToken(member.getEmail(), member.getMemberId(),
			member.getRole());
		String refreshToken = jwtService.createRefreshToken(member.getEmail(), member.getMemberId(), member.getRole());
		jwtService.updateRefreshToken(member.getEmail(), refreshToken, member.getRole());

		return LoginResponseDto.builder()
			.accessToken(accessTokenJwt)
			.refreshToken(refreshToken)
			.accessTokenExpirationTime(jwtService.getAccessTokenExpirationPeriod())
			.refreshTokenExpirationTime(jwtService.getRefreshTokenExpirationPeriod())
			.role(member.getRole())
			.build();
	}

	private Member getOrCreateMember(OAuthAttributes attributes) {
		return memberRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, attributes.getOauth2UserInfo().getId())
			.orElseGet(
				() -> memberRepository.save(
					attributes.toMemberEntity(SocialType.KAKAO, attributes.getOauth2UserInfo())));
	}
}