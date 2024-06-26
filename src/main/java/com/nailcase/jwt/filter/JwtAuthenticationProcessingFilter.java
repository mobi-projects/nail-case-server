package com.nailcase.jwt.filter;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.entity.Member;
import com.nailcase.repository.MemberRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 추가
	private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {

		String refreshToken = jwtService.extractRefreshToken(request).filter(jwtService::isTokenValid).orElse(null);

		if (refreshToken != null) {
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}
		checkAccessTokenAndAuthentication(request, response, filterChain);
	}

	private void checkRefreshTokenAndReIssueAccessToken(@NonNull HttpServletResponse response,
		@NonNull String refreshToken) {
		jwtService.extractEmail(refreshToken).ifPresent(email -> {
			String savedRefreshToken = (String)redisTemplate.opsForValue().get(email);
			if (savedRefreshToken != null && savedRefreshToken.equals(refreshToken)) {
				String reIssuedRefreshToken = reIssueRefreshToken(email);
				Member member = memberRepository.findByEmail(email)
					.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
				jwtService.sendAccessAndRefreshToken(response,
					jwtService.createAccessToken(email, member.getMemberId()),
					reIssuedRefreshToken);
			} else {
				throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
			}
		});
	}

	private String reIssueRefreshToken(String email) {
		String reIssuedRefreshToken = jwtService.createRefreshToken(email);
		jwtService.updateRefreshToken(email, reIssuedRefreshToken);
		return reIssuedRefreshToken;
	}

	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		log.info("checkAccessTokenAndAuthentication() 호출");
		jwtService.extractAccessToken(request)
			.filter(jwtService::isTokenValid)
			.flatMap(jwtService::extractEmail)
			.flatMap(memberRepository::findByEmail)
			.ifPresent(this::saveAuthentication);

		filterChain.doFilter(request, response);
	}

	private void saveAuthentication(Member myMember) {
		// 혹시 싶어서 남겨두었습니다.
		// UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
		// 	.username(String.valueOf(myMember.getMemberId()))
		// 	.password("")
		// 	.roles(myMember.getRole().name())
		// 	.build();

		// Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsUser, null,
		// 	authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

		MemberDetails memberDetails = MemberDetails.withMember(myMember);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			memberDetails, memberDetails.getPassword(), memberDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
