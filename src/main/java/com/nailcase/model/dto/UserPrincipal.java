package com.nailcase.model.dto;

import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.model.enums.Role;

public sealed interface UserPrincipal permits MemberDetails, NailArtistDetails {
	Long getId();

	String getNickname();

	String getEmail();

	Role getRole();

	default boolean isMember() {
		return this instanceof MemberDetails;
	}

	default boolean isNailArtist() {
		return this instanceof NailArtistDetails;
	}

	static void validateMember(UserDetails userDetails) {
		if (!(userDetails instanceof UserPrincipal) || !((UserPrincipal)userDetails).isMember()) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}
	}

	static void validateNailArtist(UserDetails userDetails) {
		if (!(userDetails instanceof UserPrincipal) || !((UserPrincipal)userDetails).isNailArtist()) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}
	}

	static void validateAndGetNailArtistForShop(UserDetails userDetails, Long shopId) {
		validateNailArtist(userDetails);
		NailArtistDetails nailArtistDetails = (NailArtistDetails)userDetails;
		nailArtistDetails.validateAndGetNailArtistForShop(shopId);
	}
}
