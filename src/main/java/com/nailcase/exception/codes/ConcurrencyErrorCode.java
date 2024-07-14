package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ConcurrencyErrorCode implements ErrorCodeInterface {
	CONFLICT_UPDATE(409, "리소스가 다른 요청에 의해 이미 변경됨"),
	LOCK_ACQUISITION_FAILED(408, "리소스 잠금 실패"),
	OPTIMISTIC_LOCK_ERROR(409, "데이터 버전 충돌"),
	DEADLOCK_DETECTED(410, "데드락 감지됨"),
	TRANSACTION_ROLLED_BACK(411, "트랜잭션 충돌로 롤백됨"),
	TRANSACTION_TIMEOUT(408, "트랜잭션 타임아웃"),
	TOO_MANY_REQUESTS(429, "요청 과다, 잠시 후 다시 시도하세요"),
	UPDATE_FAILURE(500, "업데이트 실패, 다시 시도하세요"),
	RETRY_LIMIT_EXCEEDED(429, "재시도 한도 초과"),
	SERIALIZATION_FAILURE(409, "데이터 직렬화 실패"),
	STATE_CONFLICT(409, "상태 불일치로 인한 충돌");

	private final int code;
	private final String message;

	ConcurrencyErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
