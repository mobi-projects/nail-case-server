package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum PostErrorCode implements ErrorCodeInterface {
	NOT_FOUND(600, "게시물을 찾을 수 없음. 요청한 게시물이 삭제되거나 찾을 수 없음."),
	COMMENT_NOT_FOUND(601, "게시물의 댓글을 찾을 수 없음"),
	LIKE_NOT_FOUND(602, "좋아요한 게시물을 찾을 수 없음"),
	UPDATE_FAILURE(603, "게시물에 대한 업데이트를 실패하였음");

	private final int code;
	private final String message;

	PostErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}