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

import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.CommonErrorCode;
import com.nailcase.exception.codes.DatabaseErrorCode;
import com.nailcase.exception.codes.ErrorCodeInterface;
import com.nailcase.exception.codes.ErrorResponse;

import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage()));

		logger.error("Validation error: {}", errors);
		return buildErrorResponse(CommonErrorCode.INVALID_INPUT, HttpStatus.BAD_REQUEST, errors);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		ErrorResponse errorResponse = new ErrorResponse(
			ex.getErrorCode().getCode(),
			ex.getErrorCode().getMessage()
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
		logger.error("AccessDeniedException: {}", ex.getMessage(), ex);
		return buildErrorResponse(AuthErrorCode.ACCESS_DENIED,
			HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ErrorResponse> handleSqlException(SQLException ex) {
		logger.error("SQLException: {}", ex.getMessage(), ex);
		return buildErrorResponse(DatabaseErrorCode.DATA_INTEGRITY_VIOLATION, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MinioException.class)
	public ResponseEntity<ErrorResponse> handleMinioException(MinioException ex) {
		logger.error("MinioException: {}", ex.getMessage(), ex);
		return buildErrorResponse(CommonErrorCode.FILE_UPLOAD_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({RuntimeException.class, Exception.class})
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
		logger.error("Unexpected exception: {}", ex.getMessage(), ex);
		return buildErrorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCodeInterface errorCode, HttpStatus status) {
		return buildErrorResponse(errorCode, status, null);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(ErrorCodeInterface errorCode, HttpStatus status,
		Map<String, String> errors) {
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), errors);
		return new ResponseEntity<>(errorResponse, status);
	}
}
