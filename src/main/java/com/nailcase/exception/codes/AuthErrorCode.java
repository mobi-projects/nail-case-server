package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum AuthErrorCode implements ErrorCodeInterface {
	INVALID_CREDENTIALS(401, "잘못된 자격 증명"),
	ACCESS_DENIED(404, "접근 거부"),
	TOKEN_EXPIRED(403, "토큰이 만료됨"),
	TOKEN_INVALID(402, "잘못된 토큰"),
	AUTH_UNEXPECTED(405, "인증과정에서 예기치 않은 에러"),
	AUTH_UNSUPPORTED(406, "지원하지 않는 로그인 서비스"),
	UNAUTHORIZED(401, "인증되지 않은 사용자");

	private final int code;
	private final String message;

	AuthErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}