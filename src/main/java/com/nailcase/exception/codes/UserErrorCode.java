package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCodeInterface {
	USER_NOT_FOUND(800, "사용자를 찾을 수 없음"),
	INVALID_USER_INPUT(801, "잘못된 사용자 입력"),
	USER_ALREADY_EXISTS(802, "사용자가 이미 존재함"),
	USER_ACCESS_DENIED(803, "사용자 접근 거부");

	private final int code;
	private final String message;

	UserErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
