package com.nailcase.exception;

import com.nailcase.exception.codes.ErrorCodeInterface;

public class BusinessException extends RuntimeException {
	private final ErrorCodeInterface errorCodeInterface;

	public BusinessException(ErrorCodeInterface errorCodeInterface) {
		super(errorCodeInterface.getMessage());
		this.errorCodeInterface = errorCodeInterface;
	}

	public ErrorCodeInterface getErrorCode() {
		return errorCodeInterface;
	}
}
