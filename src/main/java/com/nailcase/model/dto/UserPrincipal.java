package com.nailcase.model.dto;

import com.nailcase.model.enums.Role;

public interface UserPrincipal {
	Long getId();

	String getEmail();

	Role getRole();

	default boolean isMember() {
		return this instanceof MemberDetails;
	}

	default boolean isNailArtist() {
		return this instanceof NailArtistDetails;
	}
}
