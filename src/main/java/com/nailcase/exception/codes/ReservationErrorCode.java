package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ReservationErrorCode implements ErrorCodeInterface {
	RESERVATION_NOT_FOUND(1100, "예약을 찾을 수 없음"),
	INVALID_TIME_RANGE(1101, "잘못된 시간 범위"),
	INVALID_TIME(1102, "잘못된 시간"),
	RESERVATION_OVERBOOKED(1103, "예약 초과"),
	STATUS_NOT_UPDATABLE(1104, "예약 상태 변경 불가"),
	WORK_HOUR_NOT_DEFINED(1105, "예약 시간이 지정되지 않음"),
	RESERVATION_NOT_AVAILABLE(1106, "운영하는 날이 아님"),
	NOT_UPDATABLE_USER(1107, "변경 가능한 유저가 아님"),
	END_TIME_NOT_SET(1108, "종료 시간이 설정되지 않으면 확인 상태로 바꿀 수 없음");

	private final int code;
	private final String message;

	ReservationErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
