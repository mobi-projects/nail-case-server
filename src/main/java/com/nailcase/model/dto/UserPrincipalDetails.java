package com.nailcase.model.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.model.enums.Role;

import lombok.Getter;

@Getter
public final class UserPrincipalDetails extends User implements UserDetails, UserPrincipal {

	private final Long id;
	private final String email;
	private final String nickname;
	private final Role role;

	private UserPrincipalDetails(
		Long id,
		String email,
		String password,
		String nickname,
		Role role,
		Collection<? extends GrantedAuthority> authorities) {
		super(email, password, authorities);
		this.email = email;
		this.id = id;
		this.nickname = nickname;
		this.role = role;
	}

	public static UserPrincipalDetails from(UserPrincipal userPrincipal) {
		return new UserPrincipalDetails(
			userPrincipal.getId(),
			userPrincipal.getEmail(),
			"",
			userPrincipal.getNickname(),
			userPrincipal.getRole(),
			List.of(new SimpleGrantedAuthority(userPrincipal.getRole().getKey()))
		);
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getNickname() {
		return this.nickname;
	}

	@Override
	public Role getRole() {
		return this.role;
	}

	@Override
	public String getEmail() {
		return this.email;
	}
}