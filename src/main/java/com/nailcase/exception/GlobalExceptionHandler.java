package com.nailcase.exception;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.DatabaseErrorCode;
import com.nailcase.exception.codes.ErrorCodeInterface;
import com.nailcase.exception.codes.ErrorResponse;

import io.minio.errors.MinioException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<String> handleBusinessException(BusinessException ex) {
		ErrorCodeInterface ec = ex.getErrorCode();
		logger.error("BusinessException 발생: {}", ex.getMessage(), ex);
		return ResponseEntity.status(ec.getCode()).body(ec.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
		logger.error("AccessDeniedException 발생: {}", ex.getMessage(), ex);
		return buildErrorResponse(AuthErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ErrorResponse> handleSqlException(SQLException ex, WebRequest request) {
		logger.error("SQLException 발생: {}", ex.getMessage(), ex);
		return buildErrorResponse(DatabaseErrorCode.DATA_INTEGRITY_VIOLATION, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
		logger.error("Unexpected RuntimeException 발생: {}", ex.getMessage(), ex);
		return buildErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
		logger.error("Unexpected exception 발생: {}", ex.getMessage(), ex);
		return buildErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MinioException.class)
	public ResponseEntity<String> handleMinioException(MinioException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCodeInterface errorCode, HttpStatus status) {
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
		return new ResponseEntity<>(errorResponse, status);
	}
}
