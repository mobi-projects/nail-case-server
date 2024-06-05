package com.nailcase.oauth2.dto;

import java.util.UUID;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.domain.Role;
import com.nailcase.customer.domain.SocialType;
import com.nailcase.oauth2.userInfo.OAuth2UserInfo;

import lombok.Builder;
import lombok.Getter;

/**
 * 각 소셜에서 받아오는 데이터가 다르므로
 * 소셜별로 데이터를 받는 데이터를 분기 처리하는 DTO 클래스
 */
@Getter
public class OAuthAttributes {

	private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
	private OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

	@Builder
	private OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
		this.nameAttributeKey = nameAttributeKey;
		this.oauth2UserInfo = oauth2UserInfo;
	}

	public Customer toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
		return Customer.builder()
			.socialType(socialType)
			.socialId(oauth2UserInfo.getId())
			.email(UUID.randomUUID() + "@socialUser.com")
			.name(oauth2UserInfo.getName())
			.role(Role.GUEST)
			.build();
	}
}