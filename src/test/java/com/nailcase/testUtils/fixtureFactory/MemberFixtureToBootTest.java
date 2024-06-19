package com.nailcase.testUtils.fixtureFactory;

import org.springframework.stereotype.Component;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.Member;
import com.nailcase.repository.MemberRepository;
import com.nailcase.testUtils.FixtureFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberFixtureToBootTest {

	private final JwtService jwtService;

	private final MemberRepository memberRepository;

	public String createMemberAndGetJwt() {
		Member member = FixtureFactory.memberFixture.getMember();

		memberRepository.save(member);

		return jwtService.createAccessToken(member.getEmail(), member.getMemberId());
	}

	public void deleteAllMembers() {
		memberRepository.deleteAll();
	}

}
