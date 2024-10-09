package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum NotificationErrorCode implements ErrorCodeInterface {
	NOTIFICATION_CONNECTION_ERROR(2100, "알림 서비스 연결 중 오류가 발생"),
	NOTIFICATION_NOT_FOUND(2101, "알림 정보를 찾을 수 없음");

	private final int code;
	private final String message;

	NotificationErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
