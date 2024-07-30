package com.nailcase.model.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.Role;

import lombok.Getter;

@Getter
public final class MemberDetails extends User implements UserDetails, UserPrincipal {

	private final Long memberId;
	private final String email;
	private final String nickname;
	private final Role role;

	public MemberDetails(
		Long memberId,
		String email,
		String password,
		String nickname,
		Role role,
		Collection<? extends GrantedAuthority> authorities) {
		super(email, password, authorities);
		this.email = email;
		this.memberId = memberId;
		this.nickname = nickname;
		this.role = role;
	}

	public static MemberDetails withMember(Member member) {
		return new MemberDetails(
			member.getMemberId(),
			member.getEmail(),
			"",
			member.getNickname(),
			member.getRole(),
			List.of(new SimpleGrantedAuthority(member.getRole().getKey()))  // getKey() 사용
		);
	}

	@Override
	public Long getId() {
		return this.memberId;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public Role getRole() {
		return this.role;
	}

	@Override
	public String getNickname() {
		return this.nickname;
	}

}
