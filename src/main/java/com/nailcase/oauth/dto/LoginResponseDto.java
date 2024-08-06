package com.nailcase.oauth.dto;

import java.util.List;

import com.nailcase.model.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
	private String accessToken;
	private String refreshToken;
	private List<Long> shopIds;
	private boolean hasShop;
	private Role role;
	private String profileImgUrl;

	@Builder
	public LoginResponseDto(String accessToken, String refreshToken, List<Long> shopIds, boolean hasShop, Role role,
		String profileImgUrl) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.shopIds = shopIds;
		this.hasShop = hasShop;
		this.role = role;
		this.profileImgUrl = profileImgUrl;
	}

	public LoginResponseDto() {
	}
}