package com.nailcase.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nailcase.testUtils.StringGenerateFixture;

@SpringBootTest
@AutoConfigureMockMvc
class JwtTest {

	@Autowired
	private JwtService jwtService;

	@Test
	public void testTokenExpiration() {
		String email = StringGenerateFixture.makeEmail(20);
		// 현재 시간에서 1초 빼서 만료 시간을 설정
		Date now = new Date();
		Date expiredTime = new Date(now.getTime() - 1000); // 현재 시간보다 1초 전으로 설정

		// 만료된 토큰 생성
		String expiredToken = JWT.create()
			.withSubject("Test Subject")
			.withExpiresAt(expiredTime)
			.withClaim("email", email)
			.sign(Algorithm.HMAC512(jwtService.getSecretKey()));

		// 토큰 검증 시 예외가 발생해야 함
		assertThrows(JWTVerificationException.class, () -> {
			Algorithm algorithm = Algorithm.HMAC512(jwtService.getSecretKey());
			JWT.require(algorithm).build().verify(expiredToken);
		}, "Expired token should throw JWTVerificationException");
	}

}
