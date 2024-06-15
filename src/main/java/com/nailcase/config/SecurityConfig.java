package com.nailcase.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nailcase.jwt.JwtService;
import com.nailcase.jwt.filter.JwtAuthenticationProcessingFilter;
import com.nailcase.oauth2.AuditorAwareImpl;
import com.nailcase.oauth2.CustomOAuth2UserService;
import com.nailcase.oauth2.handler.OAuth2LoginFailureHandler;
import com.nailcase.oauth2.handler.OAuth2LoginSuccessHandler;
import com.nailcase.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(AbstractHttpConfigurer::disable)
			// enable h2-console
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/swagger-ui/**", "/swagger-ui/index.html", "/api-docs/**", "/webjars/**",
					"/static/**")
				.permitAll()  // Swagger와 정적 리소스 접근 허용
				.requestMatchers("/shops/**")
				.permitAll()
				.requestMatchers(PathRequest.toH2Console())
				.permitAll() // h2-console 접근 허용
				.requestMatchers("/favicon.ico")
				.permitAll()
				.requestMatchers("/oauth2/sign-up", "/login/oauth2/**")
				.permitAll()    // 권한 관련 접근 허용
				.anyRequest()
				.authenticated())    // 그 외 인증 없이 접근X
			.oauth2Login(oauth2 -> oauth2
					// TODO: OAuth2 로그인을 하면 session을 사용하게 되어 이 부분을 수정해야함.
					.loginPage("/oauth2/authorization/kakao")
					.userInfoEndpoint(userInfo -> userInfo
						.userService(customOAuth2UserService))
					.successHandler(oAuth2LoginSuccessHandler)
					.failureHandler(oAuth2LoginFailureHandler)
				// .defaultSuccessUrl("/swagger-ui/index.html", false)
			)
			.logout(logout -> logout
				.logoutSuccessUrl("/"))
			.addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		return new JwtAuthenticationProcessingFilter(jwtService, memberRepository, redisTemplate); // RedisTemplate 전달
	}

	@Bean
	public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuditorAware<Long> auditorProvider() {
		return new AuditorAwareImpl();
	}

}