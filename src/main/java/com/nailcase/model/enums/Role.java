package com.nailcase.model.enums;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
	OWNER("ROLE_OWNER"),
	MANAGER("ROLE_MANAGER"),
	MEMBER("ROLE_MEMBER");

	private final String key;

	@Override
	public String getAuthority() {
		return this.key;
	}

	public static Role fromKey(String key) {
		for (Role role : values()) {
			if (role.getKey().equals(key)) {
				return role;
			}
		}
		throw new IllegalArgumentException("Unknown role key: " + key);
	}
}