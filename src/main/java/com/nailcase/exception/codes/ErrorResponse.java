package com.nailcase.exception.codes;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	@JsonProperty("code")
	private int code;

	@JsonProperty("message")
	private String message;

	@JsonProperty("errors")
	private Map<String, String> errors;

	public ErrorResponse(int code, String message) {
		this.code = code;
		this.message = message;
		this.errors = null;
	}

	public ErrorResponse(int code, String message, Map<String, String> errors) {
		this.code = code;
		this.message = message;
		this.errors = errors;
	}
}
