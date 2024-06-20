package com.nailcase.exception;

import com.nailcase.exception.codes.ErrorCodeInterface;

public class BusinessException extends RuntimeException {
	private final ErrorCodeInterface errorCodeInterface;

	public BusinessException(ErrorCodeInterface errorCodeInterface) {
		super(errorCodeInterface.getMessage());
		this.errorCodeInterface = errorCodeInterface;
	}

	public BusinessException(ErrorCodeInterface errorCodeInterface, Throwable cause) {
		super(errorCodeInterface.getMessage(), cause);
		this.errorCodeInterface = errorCodeInterface;
	}

	public BusinessException(ErrorCodeInterface errorCodeInterface, String message) {
		super(message);
		this.errorCodeInterface = errorCodeInterface;
	}

	public ErrorCodeInterface getErrorCode() {
		return errorCodeInterface;
	}

}
