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

	public KakaoMemberLoginService(RestTemplate restTemplate, JwtService jwtService,
		MemberRepository memberRepository) {
		super(restTemplate, jwtService);
		this.memberRepository = memberRepository;
	}

	@Override
	protected LoginResponseDto processUserLogin(OAuthAttributes attributes) {
		Member member = getOrCreateMember(attributes, SocialType.KAKAO);
		String accessTokenJwt = jwtService.createAccessToken(member.getEmail(), member.getMemberId());
		String refreshToken = jwtService.createRefreshToken(member.getEmail());
		jwtService.updateRefreshToken(member.getEmail(), refreshToken);

		return LoginResponseDto.builder()
			.accessToken(accessTokenJwt)
			.refreshToken(refreshToken)
			.build();
	}

	private Member getOrCreateMember(OAuthAttributes attributes, SocialType socialType) {
		return memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
			.orElseGet(
				() -> memberRepository.save(attributes.toMemberEntity(socialType, attributes.getOauth2UserInfo())));
	}
}