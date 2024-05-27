package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum DatabaseErrorCode implements ErrorCodeInterface {
	DATA_NOT_FOUND(404, "데이터를 찾을 수 없음"),
	DUPLICATE_KEY(409, "중복 키 오류"),
	QUERY_TIMEOUT(408, "쿼리 시간 초과"),
	DATA_INTEGRITY_VIOLATION(400, "데이터 무결성 위반");

	private final int code;
	private final String message;

	DatabaseErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
