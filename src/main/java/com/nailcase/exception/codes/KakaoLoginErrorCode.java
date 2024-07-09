package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum KakaoLoginErrorCode implements ErrorCodeInterface {
	INVALID_CREDENTIALS(401, "잘못된 자격 증명"),
	KAKAO_TOKEN_NOT_FOUND(402, "카카오 응답에 액세스 토큰이 없습니다"),
	KAKAO_TOKEN_REQUEST_FAILED(403, "카카오 액세스 토큰 요청 실패"),
	KAKAO_SERVER_ERROR(404, "카카오 서버 오류"),
	UNEXPECTED_ERROR(405, "예기치 않은 오류 발생"),
	ACCESS_DENIED(406, "접근 거부"),
	TOKEN_EXPIRED(407, "토큰이 만료됨"),
	TOKEN_INVALID(408, "잘못된 토큰"),
	UNAUTHORIZED(409, "인증되지 않은 사용자");

	private final int code;
	private final String message;

	KakaoLoginErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}