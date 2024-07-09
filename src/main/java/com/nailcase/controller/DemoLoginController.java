package com.nailcase.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.jwt.JwtService;
import com.nailcase.model.dto.MemberDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.UserType;
import com.nailcase.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/demo-login")
public class DemoLoginController {
	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	@GetMapping(value = "/member", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MemberDto> loginUser() {
		Member member = memberRepository.findByEmail("member@example.com")
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		MemberDto memberDto = MemberDto.fromEntity(member);
		String accessToken = jwtService.createAccessToken(member.getEmail(), member.getMemberId(),
			UserType.MEMBER.getValue());
		String refreshToken = jwtService.createRefreshToken(member.getEmail(), UserType.MEMBER.getValue());

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + accessToken)
			.header("Refresh-Token", "Bearer " + refreshToken)
			.body(memberDto);
	}

	@GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MemberDto> loginAdmin() {
		Member admin = memberRepository.findByEmail("manager@example.com")
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		MemberDto adminDto = MemberDto.fromEntity(admin);
		String accessToken = jwtService.createAccessToken(admin.getEmail(), admin.getMemberId(),
			UserType.MANAGER.getValue());
		String refreshToken = jwtService.createRefreshToken(admin.getEmail(), UserType.MANAGER.getValue());

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + accessToken)
			.header("Refresh-Token", "Bearer " + refreshToken)
			.body(adminDto);
	}
}