package com.nailcase.exception.codes;

import lombok.Getter;

@Getter
public enum NailArtistErrorCode implements ErrorCodeInterface {
	NOT_FOUND(900, "네일아티스트가 존재하지 않음");

	private final int code;
	private final String message;

	NailArtistErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
