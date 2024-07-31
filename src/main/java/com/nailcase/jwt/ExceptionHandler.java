package com.nailcase.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ErrorResponse;
import com.nailcase.exception.codes.TokenErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.response.ResponseService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandler {

	private final ResponseService responseService;
	private final ObjectMapper objectMapper;

	public void handleException(HttpServletResponse response, BusinessException ex) throws IOException {
		HttpStatus status = determineHttpStatus(ex);
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		ErrorResponse errorResponse = responseService.getErrorResponse(ex.getErrorCode(), status.value());
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}

	private HttpStatus determineHttpStatus(BusinessException ex) {
		if (ex.getErrorCode() instanceof TokenErrorCode) {
			return HttpStatus.UNAUTHORIZED;
		} else if (ex.getErrorCode() instanceof UserErrorCode) {
			return HttpStatus.BAD_REQUEST;
		} else {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}
}
