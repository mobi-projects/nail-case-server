package com.nailcase.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nailcase.exception.codes.AuthErrorCode;

public class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

	@Test
	public void handleBusinessException() {
		BusinessException exception = new BusinessException(AuthErrorCode.ACCESS_DENIED);

		ResponseEntity<String> response = exceptionHandler.handleBusinessException(exception);

		assertEquals(HttpStatus.PAYMENT_REQUIRED, response.getStatusCode());
		assertEquals(AuthErrorCode.ACCESS_DENIED.getMessage(), response.getBody());
	}
}