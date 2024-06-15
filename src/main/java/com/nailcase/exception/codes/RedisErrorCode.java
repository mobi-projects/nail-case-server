package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum RedisErrorCode implements ErrorCodeInterface {
	REDIS_OPERATION_FAILED(1001, "Redis 작업 실패");

	private final int code;
	private final String message;

	RedisErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
