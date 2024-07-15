package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum AuthErrorCode implements ErrorCodeInterface {
	INVALID_CREDENTIALS(400, "잘못된 자격 증명"),
	ACCESS_DENIED(401, "접근 거부"),
	AUTH_UNEXPECTED(402, "인증과정에서 예기치 않은 에러"),
	AUTH_UNSUPPORTED(403, "지원하지 않는 로그인 서비스"),
	UNAUTHORIZED(404, "인증되지 않은 사용자"),
	INVALID_USER_TYPE(405, "잘못된 유저 타입");
	
	private final int code;
	private final String message;

	AuthErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}