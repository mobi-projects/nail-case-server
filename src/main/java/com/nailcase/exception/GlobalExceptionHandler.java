package com.nailcase.exception;


import com.nailcase.exception.codes.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<String> handleBusinessException(BusinessException ex) {
		ErrorCodeInterface ec = ex.getErrorCode();
		return ResponseEntity.status(ec.getCode()).body(ec.getMessage());
	}

}
