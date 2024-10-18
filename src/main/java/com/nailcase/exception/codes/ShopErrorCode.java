package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ShopErrorCode implements ErrorCodeInterface {
	SHOP_NOT_FOUND(1501, "샾을 찾을 수 없음"),
	SHOP_DELETION_FORBIDDEN(1502, "샵 삭제 권한이 없음"),
	LIKE_NOT_FOUND(1503, "좋아요한 샵을 찾을 수 없음"),
	REGISTRATION_FAILED(1504, "샵 등록 실패"),
	INVALID_SHOP_DATA(1505, "샵 등록 시 부적절한 데이터로 인한 실패"),
	CHAT_ROOM_NOT_FOUND(1506, "채팅방을 찾을 수 없음"),
	CHAT_ROOM_FORBIDDEN(1508, "채팅방 권한이 없음"),
	CHAT_ROOM_DELETION_FORBIDDEN(1509, "채팅방 삭제 권한이 없음"),
	CHAT_MESSAGE_NOT_FOUND(1510, "채팅 메시지를 찾을 수 없음"),
	CHAT_PROCESS_FAILED(1511, "채팅 처리 실패");

	private final int code;
	private final String message;

	ShopErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
