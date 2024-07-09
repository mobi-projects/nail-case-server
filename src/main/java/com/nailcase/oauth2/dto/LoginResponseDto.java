package com.nailcase.oauth2.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
	private String accessToken;
	private String refreshToken;
}
