package com.nailcase.config.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.nailcase.annotation.AuthenticatedManagerUser;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.model.enums.UserType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticatedManagerUserArgumentResolver implements HandlerMethodArgumentResolver {

	private final JwtService jwtService;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthenticatedManagerUser.class);
	}

	@Override
	public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		String token = webRequest.getHeader("Authorization");
		if (token == null) {
			throw new BusinessException(AuthErrorCode.TOKEN_NOT_FOUND);
		}

		UserType userType = jwtService.extractUserType(token)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));

		if (userType != UserType.MANAGER) {
			throw new BusinessException(AuthErrorCode.ACCESS_DENIED);
		}

		return jwtService.extractUserId(token)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
	}
}