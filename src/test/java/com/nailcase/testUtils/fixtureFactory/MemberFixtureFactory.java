package com.nailcase.testUtils.fixtureFactory;

import static org.jeasy.random.FieldPredicates.*;

import java.util.Set;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.stereotype.Component;

import com.nailcase.jwt.JwtService;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.testUtils.StringGenerateFixture;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberFixtureFactory {

	private static final Long MEMBER_ID = 1L;

	private static final String EMAIL = StringGenerateFixture.makeEmail(10);

	private static final Role ROLE = Role.GUEST;

	private final JwtService jwtService;

	private final MemberRepository memberRepository;

	public String createMemberAndGetJwt() {
		EasyRandomParameters params = new EasyRandomParameters()
			.randomize(named("memberId"), () -> MEMBER_ID)
			.randomize(named("email"), () -> EMAIL)
			.randomize(named("role"), () -> ROLE)
			.excludeField(named("shops").and(ofType(Set.class)));
		Member member = new EasyRandom(params).nextObject(Member.class);

		memberRepository.save(member);

		return jwtService.createAccessToken(member.getEmail(), member.getMemberId());
	}

	public void deleteAllMembers() {
		memberRepository.deleteAll();
	}
}
