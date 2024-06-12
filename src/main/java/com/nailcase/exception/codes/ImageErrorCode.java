package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ImageErrorCode implements ErrorCodeInterface {
	IMAGE_NOT_FOUND(700, "이미지를 찾을 수 없음"),
	UPLOAD_FAILURE(701, "이미지 업로드 실패"),
	DOWNLOAD_FAILURE(702, "이미지 다운로드 실패"),
	DELETE_FAILURE(703, "이미지 삭제 실패");

	private final int code;
	private final String message;

	ImageErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
