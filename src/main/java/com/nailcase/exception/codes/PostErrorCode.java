package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum PostErrorCode implements ErrorCodeInterface {
	NOT_FOUND(800, "게시물을 찾을 수 없음"),
	COMMENT_NOT_FOUND(801, "게시물의 댓글을 찾을 수 없음");

	private final int code;
	private final String message;

	PostErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
