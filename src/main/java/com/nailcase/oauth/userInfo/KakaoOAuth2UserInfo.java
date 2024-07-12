package com.nailcase.oauth.userInfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

	public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return attributes.get("id").toString();
	}

	@Override
	public String getNickname() {
		Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
		return properties == null ? null : (String)properties.get("nickname");
	}

	@Override
	public String getEmail() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		return kakaoAccount == null ? null : (String)kakaoAccount.get("email");
	}
}
