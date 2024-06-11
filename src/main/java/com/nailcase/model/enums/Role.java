package com.nailcase.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	OWNER("ROLE_SHOP_OWNER"),
	MANAGER("ROLE_SHOP_MANAGER"),
	USER("ROLE_SHOP_USER"),
	GUEST("ROLE_GUEST");
	
	private final String key;
}
