package com.nailcase.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {
	private String accessToken;
	private String refreshToken;
}
