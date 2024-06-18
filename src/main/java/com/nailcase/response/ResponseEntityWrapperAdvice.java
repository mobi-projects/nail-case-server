package com.nailcase.response;

import java.util.EnumSet;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class ResponseEntityWrapperAdvice implements ResponseBodyAdvice<Object> {
	private final ResponseService responseService;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

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
		if (response instanceof ServletServerHttpResponse) {
			String path = request.getURI().getPath();
			if (pathMatcher.match("/**/api-docs/**", path) || pathMatcher.match("/**/swagger-ui/**", path)) {
				return body;
			}
			HttpServletResponse servletResponse = ((ServletServerHttpResponse)response).getServletResponse();
			int status = servletResponse.getStatus();
			try {
				if (!EnumSet.of(HttpStatus.OK, HttpStatus.CREATED, HttpStatus.NO_CONTENT)
					.contains(HttpStatus.valueOf(status))) {
					return body;
				}
			} catch (IllegalArgumentException e) {
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
