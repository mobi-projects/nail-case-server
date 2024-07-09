package com.nailcase.oauth.service;

import com.nailcase.oauth.dto.LoginResponseDto;

public interface SocialLoginService {
	LoginResponseDto processLogin(String code);
}
