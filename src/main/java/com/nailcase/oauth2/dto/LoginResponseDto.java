package com.nailcase.oauth2.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
	private boolean loginSuccess;
	private String accessToken;
	private String refreshToken;
	private String userName;
	private String userEmail;
}
