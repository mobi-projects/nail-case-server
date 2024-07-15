package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCodeInterface {
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류"),
	SERVICE_UNAVAILABLE(501, "서비스를 사용할 수 없음"),
	BAD_REQUEST(502, "잘못된 요청"),
	NOT_FOUND(503, "리소스를 찾을 수 없음"),
	INVALID_INPUT(504, "유효하지 않은 값 입력"),
	FILE_UPLOAD_ERROR(505, "파일 업로드시 예상치 못한 예외 발생"),
	;

	private final int code;
	private final String message;

	CommonErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
