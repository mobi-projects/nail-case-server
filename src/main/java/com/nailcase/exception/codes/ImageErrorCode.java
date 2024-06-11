package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ImageErrorCode implements ErrorCodeInterface {
	IMAGE_UPLOAD_ERROR(1002, "이미지 업로드 작업 실패"),
	IMAGE_DELETE_ERROR(1003, "이미지 삭제 작업 실패");

	private final int code;
	private final String message;

	ImageErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
