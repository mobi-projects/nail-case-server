package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ImageErrorCode implements ErrorCodeInterface {
	IMAGE_NOT_FOUND(700, "이미지를 찾을 수 없음"),
	UPLOAD_FAILURE(701, "이미지 업로드 실패"),
	DOWNLOAD_FAILURE(702, "이미지 다운로드 실패"),
	DELETE_FAILURE(703, "이미지 삭제 실패"),
	SAVE_FAILURE(704, "이미지 저장 실패"),
	INVALID_FILE_TYPE(705, "이미지 형식이 잘못됨"),
	FILE_TOO_LARGE(706, "이미지의 크기가 큼"),
	IMAGE_LIMIT_EXCEEDED(707, "이미지의 개수가 한도를 초과");

	private final int code;
	private final String message;

	ImageErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
