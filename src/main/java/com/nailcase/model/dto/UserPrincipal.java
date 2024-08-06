package com.nailcase.model.dto;

import org.springframework.security.core.userdetails.UserDetails;

import com.nailcase.model.enums.Role;

public interface UserPrincipal extends UserDetails {
	Long id();

	String nickname();

	String email();

	Role role();

}
