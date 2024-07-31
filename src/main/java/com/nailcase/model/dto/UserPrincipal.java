package com.nailcase.model.dto;

import com.nailcase.model.enums.Role;

public interface UserPrincipal {
	Long getId();

	String getNickname();

	String getEmail();

	Role getRole();

}
