package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ValidationErrorCode implements ErrorCodeInterface {
	INVALID_INPUT(1301, "잘못된 입력"),
	MISSING_REQUIRED_FIELD(1302, "필수 필드 누락"),
	VALUE_OUT_OF_RANGE(1303, "값이 범위를 벗어남");

	private final int code;
	private final String message;

	ValidationErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
