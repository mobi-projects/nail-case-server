package com.nailcase.model.dto;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.Role;

import lombok.Getter;

@Getter
public final class NailArtistDetails extends User implements UserDetails, UserPrincipal {

	private final Long nailArtistId;
	private final String email;
	private final Role role;
	private final String nickname;
	private final Set<Long> associatedShopIds;

	public NailArtistDetails(
		Long nailArtistId,
		String email,
		String password,
		String nickname,
		Role role,
		Collection<? extends GrantedAuthority> authorities,
		Set<Long> associatedShopIds) {
		super(email, password, authorities);
		this.nailArtistId = nailArtistId;
		this.email = email;
		this.nickname = nickname;
		this.role = role;
		this.associatedShopIds = associatedShopIds;
	}

	@Override
	public Long getId() {
		return this.nailArtistId;
	}

	@Override
	public Role getRole() {
		return this.role;
	}

	@Override
	public String getNickname() {
		return this.nickname;
	}

	public static NailArtistDetails withNailArtist(NailArtist nailArtist) {
		Set<Long> shopIds = nailArtist.getShops().stream()
			.map(Shop::getShopId)
			.collect(Collectors.toSet());

		return new NailArtistDetails(
			nailArtist.getNailArtistId(),
			nailArtist.getEmail(),
			"",
			nailArtist.getNickname(),
			nailArtist.getRole(),
			List.of(new SimpleGrantedAuthority(nailArtist.getRole().getKey())),
			shopIds
		);
	}

	public void validateAndGetNailArtistForShop(Long shopId) {
		if (!this.associatedShopIds.contains(shopId)) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}
	}
}