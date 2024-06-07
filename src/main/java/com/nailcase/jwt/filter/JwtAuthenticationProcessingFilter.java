package com.nailcase.jwt.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nailcase.customer.domain.Customer;
import com.nailcase.customer.repository.CustomerRepository;
import com.nailcase.jwt.JwtService;
import com.nailcase.util.PasswordUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private static final String NO_CHECK_URL = "/login";
	private final JwtService jwtService;
	private final CustomerRepository customerRepository;

	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);
		if (refreshToken != null) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}
		if (refreshToken == null) {
			checkAccessTokenAndAuthentication(request, response, filterChain);
		}
	}

	public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		jwtService.extractEmail(refreshToken).ifPresent(email -> {
			String reIssuedRefreshToken = reIssueRefreshToken(email);
			jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(email), reIssuedRefreshToken);
		});
	}

	private String reIssueRefreshToken(String email) {
		String reIssuedRefreshToken = jwtService.createRefreshToken();
		jwtService.updateRefreshToken(email, reIssuedRefreshToken);
		return reIssuedRefreshToken;
	}

	public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		log.info("checkAccessTokenAndAuthentication() 호출");
		jwtService.extractAccessToken(request)
			.filter(jwtService::isTokenValid)
			.flatMap(jwtService::extractEmail)
			.flatMap(customerRepository::findByEmail)
			.ifPresent(this::saveAuthentication);

		filterChain.doFilter(request, response);
	}

	public void saveAuthentication(Customer myCustomer) {
		String password = myCustomer.getPassword();
		if (password == null) {
			password = PasswordUtil.generateRandomPassword();
		}

		UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
			.username(myCustomer.getEmail())
			.password(password)
			.roles(myCustomer.getRole().name())
			.build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null,
			authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
