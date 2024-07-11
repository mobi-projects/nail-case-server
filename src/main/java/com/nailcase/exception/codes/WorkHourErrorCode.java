package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum WorkHourErrorCode implements ErrorCodeInterface {
	WORK_HOUR_NOT_FOUND(404, "영업시간을 찾을 수 없음"),
	WORK_HOUR_NOT_DEFINED(400, "샵의 영업시간이 지정되지 않음"),
	NOT_OPENED(400, "운영중인 영업시간이 아님");

	private final int code;
	private final String message;

	WorkHourErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
