package com.nailcase.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse {
	private boolean success;
	private int code;
	private String message;

	public static final int SUCCESS_CODE = 200;
	public static final String SUCCESS_MESSAGE = "SUCCESS";
}
