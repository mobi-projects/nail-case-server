package com.nailcase.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nailcase.config.web.AuthenticatedManagerUserArgumentResolver;
import com.nailcase.config.web.AuthenticatedUserArgumentResolver;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final AuthenticatedManagerUserArgumentResolver authenticatedManagerUserArgumentResolver;
	private final AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(authenticatedManagerUserArgumentResolver);
		resolvers.add(authenticatedUserArgumentResolver);
	}
}