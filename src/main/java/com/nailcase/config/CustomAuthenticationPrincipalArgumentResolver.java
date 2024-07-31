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

import com.nailcase.model.dto.UserPrincipalDetails;

public class CustomAuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(Long.class) &&
			parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
	}

	@Override
	public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
		@NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		UserPrincipalDetails principal = (UserPrincipalDetails)authentication.getPrincipal();
		return principal.getId();
	}
}
