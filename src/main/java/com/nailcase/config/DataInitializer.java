package com.nailcase.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.SocialType;
import com.nailcase.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

	private final MemberRepository memberRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		createDemoAccount("user@example.com", "User", Role.USER);
		createDemoAccount("admin@example.com", "Admin", Role.OWNER);
	}

	private void createDemoAccount(String email, String name, Role role) {
		if (!memberRepository.existsByEmail(email)) {
			Member member = Member.builder()
				.name(name)
				.email(email)
				.role(role)
				.profileImgUrl(
					"https://github.com/mobi-projects/nail-case-server/assets/96242198/5c306514-6a10-4887-98cf-6e897a2f063c")
				.socialType(SocialType.KAKAO)
				.socialId("3588226794")
				.build();

			memberRepository.save(member);
		}
	}
}