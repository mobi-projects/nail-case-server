package com.nailcase.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.enums.Role;

public class CustomAuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
	}

	@Override
	public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
		@NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}

		Object principal = authentication.getPrincipal();
		Class<?> parameterType = parameter.getParameterType();

		if (parameterType.equals(Long.class)) {
			return ((UserPrincipal)principal).id();
		} else if (parameterType.equals(Role.class)) {
			return ((UserPrincipal)principal).role();
		} else if (parameterType.equals(UserPrincipal.class)) {
			return principal;
		}

		throw new IllegalArgumentException("지원하지 않는 타입: " + parameterType);
	}
}