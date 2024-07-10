package com.nailcase.oauth.dto;

import java.util.List;

import com.nailcase.model.enums.UserType;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
	private String accessToken;
	private String refreshToken;
	private List<Long> shopIds;
	private boolean hasShop;
	private UserType userType;
	private long accessTokenExpirationTime;
	private long refreshTokenExpirationTime;
}