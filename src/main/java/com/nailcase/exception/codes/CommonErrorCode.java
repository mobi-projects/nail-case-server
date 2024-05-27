package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCodeInterface {
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류"),
	SERVICE_UNAVAILABLE(503, "서비스를 사용할 수 없음"),
	BAD_REQUEST(400, "잘못된 요청"),
	NOT_FOUND(404, "리소스를 찾을 수 없음");

	private final int code;
	private final String message;

	CommonErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
