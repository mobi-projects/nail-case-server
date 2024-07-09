package com.nailcase.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nailcase.jwt.JwtService;
import com.nailcase.jwt.filter.JwtAuthenticationProcessingFilter;
import com.nailcase.oauth.AuditorAwareImpl;
import com.nailcase.oauth.CustomOAuth2UserService;
import com.nailcase.oauth.handler.OAuth2LoginFailureHandler;
import com.nailcase.oauth.handler.OAuth2LoginSuccessHandler;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;
	private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입

	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(Customizer.withDefaults())
			// enable h2-console
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/swagger-ui/**", "/swagger-ui/index.html", "/api-docs/**", "/webjars/**",
					"/static/**", "/auth/**")
				.permitAll()  // Swagger와 정적 리소스 접근 허용
				.requestMatchers("/shops/**")
				.permitAll()
				.requestMatchers(PathRequest.toH2Console())
				.permitAll() // h2-console 접근 허용
				.requestMatchers("/favicon.ico")
				.permitAll()
				.requestMatchers("/oauth2/sign-up", "/login/oauth2/**")
				.permitAll()    // 권한 관련 접근 허용
				.requestMatchers("/demo-login/**") // 데모 테스트용
				.permitAll()
				.anyRequest()
				.authenticated())    // 그 외 인증 없이 접근X
			// .oauth2Login(oauth2 -> oauth2
			// 		// TODO: OAuth2 로그인을 하면 session을 사용하게 되어 이 부분을 수정해야함.
			// 		.loginPage("/oauth2/authorization/kakao")
			// 		.userInfoEndpoint(userInfo -> userInfo
			// 			.userService(customOAuth2UserService))
			// 		.successHandler(oAuth2LoginSuccessHandler)
			// 		.failureHandler(oAuth2LoginFailureHandler)
			// 	// .defaultSuccessUrl("/swagger-ui/index.html", false)
			// )
			// .logout(logout -> logout
			// 	.logoutSuccessUrl("/"))
			.addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		return new JwtAuthenticationProcessingFilter(jwtService, memberRepository, nailArtistRepository,
			redisTemplate); // RedisTemplate 전달
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

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("http://localhost:3000");
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}