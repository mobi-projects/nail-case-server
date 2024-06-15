package com.nailcase.response;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class ResponseEntityWrapperAdvice implements ResponseBodyAdvice<Object> {
	private final ResponseService responseService;

	@Override
	public boolean supports(@NonNull MethodParameter returnType,
		@Nullable Class<? extends HttpMessageConverter<?>> converterType) {
		// Advice를 적용할 클래스를 명시적으로 제한
		return SingleResponse.class.isAssignableFrom(returnType.getParameterType()) ||
			ListResponse.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
		@Nullable MediaType selectedContentType,
		@Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
		@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
		// ResponseEntity는 바로 반환
		if (body instanceof ResponseEntity<?>) {
			return body;
		}

		// HttpServletResponse를 사용하여 상태 코드 확인
		if (response instanceof ServletServerHttpResponse) {
			HttpServletResponse servletResponse = ((ServletServerHttpResponse)response).getServletResponse();
			int status = servletResponse.getStatus();

			// 상태 코드가 OK(200) 이외의 경우, 원본 body 반환
			if (status != HttpStatus.OK.value()) {
				return body;
			}

			// List 타입 처리
			if (body instanceof List<?>) {
				return responseService.getListResponse((List<?>)body);
			}

			// Single 객체 처리
			return responseService.getSingleResponse(body);
		}

		return body;
	}
}
