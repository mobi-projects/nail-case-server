package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ReservationErrorCode implements ErrorCodeInterface {
	RESERVATION_NOT_FOUND(404, "예약을 찾을 수 없음"),
	INVALID_TIME_RANGE(400, "잘못된 시간 범위"),
	RESERVATION_OVERBOOKED(409, "예약 초과"),
	STATUS_NOT_UPDATABLE(409, "예약 상태 변경 불가");

	private final int code;
	private final String message;

	ReservationErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
