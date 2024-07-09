package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum WorkHourErrorCode implements ErrorCodeInterface {
	WORK_HOUR_NOT_FOUND(404, "영업시간을 찾을 수 없음");

	private final int code;
	private final String message;

	WorkHourErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
