package com.nailcase.customer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	OWNER("ROLE_SHOP_OWNER"), MANAGER("ROLE_SHOP_MANAGER"), GUEST("ROLE_GUEST");
	private final String key;

}
