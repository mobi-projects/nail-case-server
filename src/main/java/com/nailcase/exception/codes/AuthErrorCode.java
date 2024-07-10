package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum AuthErrorCode implements ErrorCodeInterface {
	INVALID_CREDENTIALS(401, "잘못된 자격 증명"),
	ACCESS_DENIED(402, "접근 거부"),
	ACCESS_RETRIEVE(403, "액세스 토큰 발급 에러"),
	TOKEN_EXPIRED(404, "토큰이 만료됨"),
	TOKEN_INVALID(405, "잘못된 토큰"),
	AUTH_UNEXPECTED(406, "인증과정에서 예기치 않은 에러"),
	AUTH_UNSUPPORTED(407, "지원하지 않는 로그인 서비스"),
	UNAUTHORIZED(408, "인증되지 않은 사용자"),
	INVALID_USER_TYPE(409, "잘못된 유저 타입"),
	TOKEN_NOT_FOUND(410, "잘못된 토큰");

	private final int code;
	private final String message;

	AuthErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}