package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum TokenErrorCode implements ErrorCodeInterface {
	ACCESS_TOKEN_RETRIEVE(1701, "액세스 토큰 발급 에러"),
	REFRESH_TOKEN_RETRIEVE(1702, "리프레쉬 토큰 발급 에러"),
	ACCESS_TOKEN_EXPIRED(1703, "액세스 토큰이 만료됨"),
	REFRESH_TOKEN_EXPIRED(1704, "리프레쉬 토큰이 만료됨"),
	TOKEN_INVALID(1705, "유효하지 않은 토큰"),
	TOKEN_NOT_FOUND(1706, "토큰을 찾을 수 없음"),
	ACCESS_TOKEN_SAVE_FAILED(1707, "액세스 토큰 저장 실패"),
	REFRESH_TOKEN_SAVE_FAILED(1708, "리프레쉬 토큰 저장 실패"),
	TOKEN_MALFORMED(1709, "잘못된 형식의 토큰"),
	TOKEN_UNSUPPORTED(1710, "지원하지 않는 토큰"),
	TOKEN_SIGNATURE_INVALID(1711, "토큰 서명이 유효하지 않음"),
	API_CONNECTION_ERROR(1712, "API 연결 오류"),
	API_ENDPOINT_NOT_FOUND(1713, "API 엔드포인트를 찾을 수 없음");

	private final int code;
	private final String message;

	TokenErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}