package com.nailcase.model.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;

import lombok.Getter;

@Getter
public class NailArtistDetails extends User implements UserDetails, UserPrincipal {

	private final Long nailArtistId;
	private final String email;
	private final Role role;
	private final NailArtist nailArtist;

	public NailArtistDetails(
		Long nailArtistId,
		String username,
		String password,
		Role role,
		Collection<? extends GrantedAuthority> authorities,
		NailArtist nailArtist) {
		super(username, password, authorities);
		this.nailArtistId = nailArtistId;
		this.email = username;
		this.role = role;
		this.nailArtist = nailArtist;
	}

	public static NailArtistDetails withNailArtist(NailArtist nailArtist) {
		return new NailArtistDetails(
			nailArtist.getNailArtistId(),
			nailArtist.getEmail(),
			"",
			nailArtist.getRole(),
			List.of(new SimpleGrantedAuthority(nailArtist.getRole().getKey())),
			nailArtist
		);
	}

	@Override
	public Long getId() {
		return this.nailArtistId;
	}

	@Override
	public Role getRole() {
		return this.role;
	}

	public NailArtist validateAndGetNailArtistForShop(Long shopId) {
		if (!this.nailArtist.getShop().getShopId().equals(shopId)) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}
		return this.nailArtist;
	}

	public void validateNailArtist(UserDetails userDetails) {
		if (!(userDetails instanceof UserPrincipal) || !((UserPrincipal)userDetails).isNailArtist()) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}
	}

}