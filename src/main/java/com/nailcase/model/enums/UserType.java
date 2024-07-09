package com.nailcase.model.enums;

import lombok.Getter;

@Getter
public enum UserType {
	MEMBER("member"),
	MANAGER("manager");

	private final String value;

	UserType(String value) {
		this.value = value;
	}
}