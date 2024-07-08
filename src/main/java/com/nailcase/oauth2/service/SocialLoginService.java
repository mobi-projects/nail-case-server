package com.nailcase.oauth2.service;

import com.nailcase.oauth2.dto.LoginResponseDto;

public interface SocialLoginService {
	LoginResponseDto processLogin(String code);
}
