package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum UnixTimeErrorCode implements ErrorCodeInterface {
	NULL_DATETIME(4001, "DateTime 값이 null"),
	NULL_TIMESTAMP(4002, "타임스탬프 값이 null"),
	NEGATIVE_TIMESTAMP(4003, "타임스탬프 값이 음수"),
	TIMESTAMP_TOO_LARGE(4004, "타임스탬프 값이 너무 큼"),
	INVALID_TIMESTAMP_FORMAT(4005, "유효하지 않은 타임스탬프 형식"),
	CONVERSION_ERROR(4006, "시간 변환 중 오류가 발생");

	private final int code;
	private final String message;

	UnixTimeErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
