package com.nailcase.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import com.nailcase.model.enums.Role;

import lombok.Getter;

/**
 * DefaultOAuth2User를 상속하고, email과 role, memberId 필드를 추가로 가진다.
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

	private final String email;
	private final Long memberId;
	private Role role;

	public CustomOAuth2User(
		Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes,
		String nameAttributeKey,
		String email,
		Role role,
		Long memberId
	) {
		super(authorities, attributes, nameAttributeKey);
		this.email = email;
		this.role = role;
		this.memberId = memberId;
	}

	public void updateRole(Role role) {
		this.role = role;
	}
}
