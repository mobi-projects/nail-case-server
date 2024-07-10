package com.nailcase.model.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.UserType;

import lombok.Getter;

@Getter
public class NailArtistDetails extends User implements UserDetails, UserPrincipal {

	private final Long nailArtistId;
	private final String email;
	private final Role role;

	public NailArtistDetails(
		Long nailArtistId,
		String username,
		String password,
		Role role,
		Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.nailArtistId = nailArtistId;
		this.email = username;
		this.role = role;
	}

	public static NailArtistDetails withNailArtist(NailArtist nailArtist) {
		return new NailArtistDetails(
			nailArtist.getNailArtistId(),
			nailArtist.getEmail(),
			"",
			nailArtist.getRole(),
			List.of(new SimpleGrantedAuthority(nailArtist.getRole().name()))
		);
	}

	@Override
	public Long getId() {
		return this.nailArtistId;
	}

	@Override
	public UserType getUserType() {
		return UserType.MANAGER;
	}
}