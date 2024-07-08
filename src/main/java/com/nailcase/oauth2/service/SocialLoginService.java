package com.nailcase.oauth2.service;

import java.util.Map;

public interface SocialLoginService {
	Map<String, String> processLogin(String code);
}
