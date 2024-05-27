package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ValidationErrorCode implements ErrorCodeInterface {
	INVALID_INPUT(400, "잘못된 입력"),
	MISSING_REQUIRED_FIELD(400, "필수 필드 누락"),
	VALUE_OUT_OF_RANGE(400, "값이 범위를 벗어남");

	private final int code;
	private final String message;

	ValidationErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
