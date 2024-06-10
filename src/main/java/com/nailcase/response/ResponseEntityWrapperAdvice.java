package com.nailcase.response;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@ControllerAdvice
@RequiredArgsConstructor
public class ResponseEntityWrapperAdvice implements ResponseBodyAdvice<Object> {
	private final ResponseService responseService;

	@Override
	public boolean supports(
		@NonNull MethodParameter returnType,
		@Nullable Class<? extends HttpMessageConverter<?>> converterType
	) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(
		Object body,
		@NonNull MethodParameter returnType,
		@Nullable MediaType selectedContentType,
		@Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
		@NonNull ServerHttpRequest request,
		@NonNull ServerHttpResponse response
	) {
		if (request.getURI().getPath().equals("/api/v1/v3/api-docs")) {
			return body; // 원본 데이터 반환
		}
		if (response instanceof ServletServerHttpResponse) {
			HttpServletResponse servletResponse = ((ServletServerHttpResponse)response).getServletResponse();
			int status = servletResponse.getStatus();
			if (status != HttpStatus.OK.value()) {
				return body;
			}

			if (body instanceof List<?>) {
				return responseService.getListResponse((List<?>)body);
			} else {
				return responseService.getSingleResponse(body);
			}
		}

		return body;
	}
}