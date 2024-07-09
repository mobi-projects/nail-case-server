package com.nailcase.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ManagerRole {
	MANAGER("ROLE_SHOP_MANAGER");
	private final String key;
}