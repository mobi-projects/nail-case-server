package com.nailcase.testUtils.fixture;

import org.springframework.stereotype.Component;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.UserType;
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

		return jwtService.createAccessToken(member.getEmail(), member.getMemberId(), UserType.MEMBER.getValue());
	}

	public void deleteAllMembers() {
		memberRepository.deleteAll();
	}

}
