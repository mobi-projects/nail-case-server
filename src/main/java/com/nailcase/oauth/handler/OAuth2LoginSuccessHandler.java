// package com.nailcase.oauth.handler;
//
// import org.springframework.security.core.Authentication;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.stereotype.Component;
//
// import com.nailcase.exception.BusinessException;
// import com.nailcase.jwt.JwtService;
// import com.nailcase.oauth.CustomOAuth2User;
//
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Component
// @RequiredArgsConstructor
// //@Transactional
// public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
//
// 	private final JwtService jwtService;
//
// 	@Override
// 	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
// 		Authentication authentication) throws BusinessException {
// 		log.info("OAuth2 Login 성공!");
// 		CustomOAuth2User oAuth2User = (CustomOAuth2User)authentication.getPrincipal();
//
// 		// User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
// 		/*if (oAuth2User.getRole() == Role.GUEST) {
// 			String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
// 			response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
// 			response.sendRedirect("oauth2/sign-up"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
//
// 			jwtService.sendAccessAndRefreshToken(response, accessToken, null);
// 		} else {
// 			loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
// 		}*/
//
// 		loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
//
// 	}
//
// 	// TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
// 	private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws BusinessException {
// 		String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(), oAuth2User.getMemberId());
// 		String refreshToken = jwtService.createRefreshToken(oAuth2User.getEmail());
// 		response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
// 		response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
// 		// 개발 할때 토큰 복붙해서 사용하는 용도
// 		log.info("accessToken => Bearer {}", accessToken);
// 		log.info("refreshToken => Bearer {}", refreshToken);
// 		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
// 		jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
// 	}
// }