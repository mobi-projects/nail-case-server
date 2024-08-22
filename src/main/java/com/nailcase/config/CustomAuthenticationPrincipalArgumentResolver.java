package com.nailcase.config;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

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
		if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
			return handleAnonymousUser(parameter);
		}

		Object principal = authentication.getPrincipal();
		if (!(principal instanceof UserPrincipal)) {
			return handleAnonymousUser(parameter);
		}

		UserPrincipal userPrincipal = (UserPrincipal)principal;
		Class<?> parameterType = parameter.getParameterType();

		if (parameterType.equals(Optional.class)) {
			return handleOptionalType(parameter, userPrincipal);
		} else if (parameterType.equals(Long.class)) {
			return userPrincipal.id();
		} else if (parameterType.equals(Role.class)) {
			return userPrincipal.role();
		} else if (parameterType.equals(UserPrincipal.class)) {
			return userPrincipal;
		}

		throw new IllegalArgumentException("지원하지 않는 타입: " + parameterType);
	}

	private Object handleAnonymousUser(MethodParameter parameter) {
		Class<?> parameterType = parameter.getParameterType();
		if (parameterType.equals(Optional.class)) {
			return Optional.empty();
		} else if (parameterType.equals(Long.class) || parameterType.equals(Role.class) || parameterType.equals(
			UserPrincipal.class)) {
			return null;
		}
		throw new IllegalArgumentException("익명 사용자에 대해 지원하지 않는 타입: " + parameterType);
	}

	private Object handleOptionalType(MethodParameter parameter, UserPrincipal userPrincipal) {
		Type genericType = parameter.getGenericParameterType();
		if (genericType instanceof ParameterizedType) {
			Type[] actualTypeArguments = ((ParameterizedType)genericType).getActualTypeArguments();
			if (actualTypeArguments.length == 1) {
				Class<?> actualTypeArgument = (Class<?>)actualTypeArguments[0];
				if (actualTypeArgument.equals(Long.class)) {
					return Optional.of(userPrincipal.id());
				} else if (actualTypeArgument.equals(Role.class)) {
					return Optional.of(userPrincipal.role());
				} else if (actualTypeArgument.equals(UserPrincipal.class)) {
					return Optional.of(userPrincipal);
				}
			}
		}
		throw new IllegalArgumentException("지원하지 않는 Optional 타입: " + genericType);
	}
}