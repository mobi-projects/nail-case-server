package com.nailcase.oauth2;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.NonNull;

public class AuditorAwareImpl implements AuditorAware<Long> {

	@NonNull
	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null
			|| !authentication.isAuthenticated()
			|| authentication.getPrincipal().equals("anonymousUser")
		) {
			return Optional.empty();
		}

		if (authentication.getPrincipal() instanceof CustomOAuth2User user) {
			return Optional.of(user.getMemberId());
		}

		return Optional.empty();
	}
}

