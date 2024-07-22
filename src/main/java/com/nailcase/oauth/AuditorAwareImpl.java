package com.nailcase.oauth;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nailcase.model.dto.UserPrincipal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<Long> {

	@NotNull
	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal()
			.equals("anonymousUser")) {
			log.debug("No authenticated user found, returning empty auditor");
			return Optional.empty();
		}

		if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
			log.debug("Returning auditor ID: {}", userPrincipal.getId());
			return Optional.of(userPrincipal.getId());
		}

		log.debug("Unknown principal type: {}", authentication.getPrincipal().getClass());
		return Optional.empty();
	}
}