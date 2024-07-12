package com.nailcase.oauth.dto;

import java.util.List;

import com.nailcase.model.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
	private String accessToken;
	private String refreshToken;
	private List<Long> shopIds;
	private boolean hasShop;
	private Role role;
	private long accessTokenExpirationTime;
	private long refreshTokenExpirationTime;
}