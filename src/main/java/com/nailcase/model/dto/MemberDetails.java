package com.nailcase.model.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.Role;

import lombok.Getter;

@Getter
public class MemberDetails extends User implements UserDetails, UserPrincipal {

	private final Long memberId;
	private final String email;
	private final Role role;

	public MemberDetails(
		Long memberId,
		String username,
		String password,
		Role role,
		Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.memberId = memberId;
		this.email = username;
		this.role = role;
	}

	public static MemberDetails withMember(Member member) {
		return new MemberDetails(
			member.getMemberId(),
			member.getEmail(),
			"",
			member.getRole(),
			List.of(new SimpleGrantedAuthority(member.getRole().getKey()))  // getKey() 사용
		);
	}

	@Override
	public Long getId() {
		return this.memberId;
	}

	@Override
	public Role getRole() {
		return this.role;
	}

	public void validateMember(UserDetails userDetails) {
		if (!(userDetails instanceof UserPrincipal) || !((UserPrincipal)userDetails).isMember()) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}
	}
}
