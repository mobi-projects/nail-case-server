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
import com.nailcase.model.dto.NailArtistDto;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/demo-login")
public class DemoLoginController {
	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;

	@GetMapping(value = "/member", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MemberDto> loginUser() {
		Member member = memberRepository.findByEmail("member@example.com")
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		MemberDto memberDto = MemberDto.fromEntity(member);
		String accessToken = jwtService.createAccessToken(member.getEmail(), member.getMemberId(),
			member.getRole());
		String refreshToken = jwtService.createRefreshToken(member.getEmail(), member.getMemberId(), member.getRole());
		jwtService.updateRefreshToken(member.getEmail(), refreshToken, member.getRole());

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + accessToken)
			.header("Refresh-Token", "Bearer " + refreshToken)
			.body(memberDto);
	}

	@GetMapping(value = "/manager", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NailArtistDto> loginAdmin() {
		NailArtist manager = nailArtistRepository.findByEmail("manager@example.com")
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

		NailArtistDto adminDto = NailArtistDto.fromEntity(manager);
		String accessToken = jwtService.createAccessToken(manager.getEmail(), manager.getNailArtistId(),
			manager.getRole()
		);
		String refreshToken = jwtService.createRefreshToken(manager.getEmail(), manager.getNailArtistId(),
			manager.getRole());
		jwtService.updateRefreshToken(manager.getEmail(), refreshToken, manager.getRole());

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + accessToken)
			.header("Refresh-Token", "Bearer " + refreshToken)
			.body(adminDto);
	}
}