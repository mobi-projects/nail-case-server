package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ConcurrencyErrorCode implements ErrorCodeInterface {
	CONFLICT_UPDATE(2201, "리소스가 다른 요청에 의해 이미 변경됨"),
	LOCK_ACQUISITION_FAILED(2202, "리소스 잠금 실패"),
	OPTIMISTIC_LOCK_ERROR(2203, "데이터 버전 충돌"),
	DEADLOCK_DETECTED(2204, "데드락 감지됨"),
	TRANSACTION_ROLLED_BACK(2205, "트랜잭션 충돌로 롤백됨"),
	TRANSACTION_TIMEOUT(2206, "트랜잭션 타임아웃"),
	TOO_MANY_REQUESTS(2207, "요청 과다, 잠시 후 다시 시도하세요"),
	UPDATE_FAILURE(2208, "업데이트 실패, 다시 시도하세요"),
	RETRY_LIMIT_EXCEEDED(2209, "재시도 한도 초과"),
	SERIALIZATION_FAILURE(2210, "데이터 직렬화 실패"),
	STATE_CONFLICT(2211, "상태 불일치로 인한 충돌");

	private final int code;
	private final String message;

	ConcurrencyErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
