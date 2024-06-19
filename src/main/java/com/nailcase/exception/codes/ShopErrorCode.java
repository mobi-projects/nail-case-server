package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum ShopErrorCode implements ErrorCodeInterface {
	SHOP_NOT_FOUND(404, "샾을 찾을 수 없음");

	private final int code;
	private final String message;

	ShopErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
