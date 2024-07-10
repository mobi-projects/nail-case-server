package com.nailcase.model.dto;

import com.nailcase.model.enums.UserType;

public interface UserPrincipal {
	Long getId();

	String getEmail();

	UserType getUserType();
}
