package com.nailcase.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	OWNER("ROLE_OWNER"),
	MANAGER("ROLE_MANAGER"),
	MEMBER("ROLE_MEMBER");
	private final String key;
}