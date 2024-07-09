package com.nailcase.oauth.dto;

import java.util.Map;

import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.SocialType;
import com.nailcase.oauth.userInfo.KakaoOAuth2UserInfo;
import com.nailcase.oauth.userInfo.OAuth2UserInfo;

import lombok.Builder;
import lombok.Getter;

/**
 * 각 소셜에서 받아오는 데이터가 다르므로
 * 소셜별로 데이터를 받는 데이터를 분기 처리하는 DTO 클래스
 */
@Getter
public class OAuthAttributes {

	private final String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
	private final OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)
	private final Map<String, Object> attributes;

	@Builder
	private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo, Map<String, Object> attributes) {
		this.nameAttributeKey = nameAttributeKey;
		this.oauth2UserInfo = oauth2UserInfo;
		this.attributes = attributes;
	}

	public static OAuthAttributes of(
		String registrationId, String userNameAttributeName,
		Map<String, Object> attributes
	) {
		if ("kakao".equals(registrationId)) {
			return ofKakao(userNameAttributeName, attributes);
		}
		// facebook 추가
		throw new IllegalArgumentException("Unknown registrationId: " + registrationId);
	}

	private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
			.nameAttributeKey(userNameAttributeName)
			.oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
			.attributes(attributes)
			.build();
	}

	public Member toMemberEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
		return Member.builder()
			.socialType(socialType)
			.socialId(oauth2UserInfo.getId())
			.email(oauth2UserInfo.getEmail() != null ? oauth2UserInfo.getEmail() :
				oauth2UserInfo.getId() + "@socialuser.com")
			.name(oauth2UserInfo.getName())
			.role(Role.MEMBER)
			.build();
	}

	public NailArtist toManagerEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
		return NailArtist.builder()
			.socialType(socialType)
			.socialId(oauth2UserInfo.getId())
			.email(oauth2UserInfo.getEmail() != null ? oauth2UserInfo.getEmail() :
				oauth2UserInfo.getId() + "@socialuser.com")
			.name(oauth2UserInfo.getName())
			.role(Role.MANAGER)
			.build();
	}
}