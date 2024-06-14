package com.nailcase.model.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.nailcase.model.entity.Member;

import lombok.Getter;

@Getter
public class MemberDetails extends User {

	private final Long memberId;

	private final String email;

	public MemberDetails(
		Long memberId,
		String username,
		String password,
		Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.memberId = memberId;
		this.email = username;
	}

	public static MemberDetails withMember(Member member) {
		return new MemberDetails(
			member.getMemberId(),
			member.getEmail(),
			"",
			List.of(new SimpleGrantedAuthority(member.getRole().name()))
		);
	}

}
