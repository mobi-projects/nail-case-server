package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ShopInfoErrorCode implements ErrorCodeInterface {
	SHOP_INFO_NOT_FOUND(404, "샾정보를 찾을 수 없음");

	private final int code;
	private final String message;

	ShopInfoErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}