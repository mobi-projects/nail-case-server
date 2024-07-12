package com.nailcase.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenException extends AuthenticationException {
	public TokenException(String msg) {
		super(msg);
	}

	public TokenException(String msg, Throwable t) {
		super(msg, t);
	}
}

