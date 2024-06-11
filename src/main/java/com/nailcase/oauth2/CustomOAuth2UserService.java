package com.nailcase.oauth2;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.SocialType;
import com.nailcase.oauth2.dto.OAuthAttributes;
import com.nailcase.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
			.getUserInfoEndpoint().getUserNameAttributeName();

		OAuthAttributes attributes = OAuthAttributes.of(
			registrationId,
			userNameAttributeName,
			oAuth2User.getAttributes()
		);

		Member member = saveOrUpdate(attributes);

		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
			attributes.getAttributes(),
			attributes.getNameAttributeKey(),
			member.getEmail(),
			member.getRole()
		);
	}

	private Member saveOrUpdate(OAuthAttributes attributes) {
		/*Member member = memberRepository.findByEmail(attributes.getOauth2UserInfo().getEmail())
			.map(entity -> entity.update(attributes.getOauth2UserInfo().getName()))
			.orElse(attributes.toEntity(SocialType.KAKAO, attributes.getOauth2UserInfo()));*/
		Member member = attributes.toEntity(SocialType.KAKAO, attributes.getOauth2UserInfo());
		return memberRepository.save(member);
	}
}
