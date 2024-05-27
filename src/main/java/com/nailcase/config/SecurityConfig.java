package com.nailcase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)

			// enable h2-console
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			)

			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/swagger-ui/**", "/swagger", "/v3/api-docs/**", "/webjars/**", "/static/**").permitAll()  // Swagger와 정적 리소스 접근 허용
				.requestMatchers("/api/login").permitAll() // 로그인 api
				.requestMatchers("/api/signup").permitAll() // 회원가입 api
				// .requestMatchers(PathRequest.toH2Console()).permitAll() // h2-console 접근 허용
				.requestMatchers("/favicon.ico").permitAll()
				.anyRequest().authenticated() // 그 외 인증 없이 접근X
			);
		return http.build();
	}

}
