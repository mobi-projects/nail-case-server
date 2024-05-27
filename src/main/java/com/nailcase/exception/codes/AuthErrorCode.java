package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum AuthErrorCode implements ErrorCodeInterface {
	INVALID_CREDENTIALS(401, "잘못된 자격 증명"),
	ACCESS_DENIED(403, "접근 거부"),
	TOKEN_EXPIRED(401, "토큰이 만료됨"),
	TOKEN_INVALID(401, "잘못된 토큰");

	private final int code;
	private final String message;

	AuthErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
