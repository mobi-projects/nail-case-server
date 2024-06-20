package com.nailcase.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;

public class SecurityUtil {

	public static Long getCurrentMemberEmail() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication.getName() == null) {
			throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
		}

		return Long.parseLong(authentication.getName());
	}
}