package com.nailcase.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nailcase.jwt.JwtProcessingFilter;
import com.nailcase.jwt.JwtService;
import com.nailcase.jwt.JwtTokenProcessor;
import com.nailcase.oauth.AuditorAwareImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtTokenProcessor jwtTokenProcessor;
	private final JwtService jwtService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf
				.ignoringRequestMatchers("/chat", "/ws/**", "/ws/chat/**", "/stomp/**", "/stomp/chat/**",
					"/stomp/chat/info")
				.disable())
			.cors(Customizer.withDefaults())
			// enable h2-console
			.headers(headers -> headers
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/swagger-ui/**", "/swagger-ui/index.html", "/api-docs/**", "/webjars/**",
					"/static/**", "/auth/**", "/main/**", "**")
				.permitAll()  // Swagger와 정적 리소스 접근 허용
				.requestMatchers(PathRequest.toH2Console())
				.permitAll() // h2-console 접근 허용
				.requestMatchers("/favicon.ico")
				.permitAll()
				.requestMatchers("/oauth2/sign-up", "/login/oauth2/**")
				.permitAll()    // 권한 관련 접근 허용
				.requestMatchers("/demo-login/**", "/chat", "/ws/**", "/ws/chat/**", "/stomp/**", "/stomp/chat/**",
					"/stomp/chat/info") // 데모 테스트용
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/shops/*/reservations")
				.permitAll()
				.requestMatchers(HttpMethod.PATCH, "shops/*/reservations/*/confirm", "shops/*/reservations/*/reject",
					"shops/*/reservations/*/complete")
				.hasRole("MANAGER")
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
			// .addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
			// .addFilterBefore(exceptionTranslationFilter(), JwtAuthenticationProcessingFilter.class);

			.addFilterBefore(jwtProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public JwtProcessingFilter jwtProcessingFilter() {
		return new JwtProcessingFilter(jwtTokenProcessor, jwtService);
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
		configuration.setAllowedOrigins(Arrays.asList(
			"http://localhost:3000",
			"https://nail-case-client.vercel.app",
			"http://localhost:8081"
		));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("*");
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
