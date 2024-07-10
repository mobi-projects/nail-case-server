package com.nailcase.model.enums;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;

import lombok.Getter;

@Getter
public enum UserType {
	MEMBER("member"),
	MANAGER("manager");

	private final String value;

	UserType(String value) {
		this.value = value;
	}

	public static UserType fromString(String value) {
		for (UserType type : UserType.values()) {
			if (type.getValue().equalsIgnoreCase(value)) {
				return type;
			}
		}
		throw new BusinessException(AuthErrorCode.INVALID_USER_TYPE);
	}
}