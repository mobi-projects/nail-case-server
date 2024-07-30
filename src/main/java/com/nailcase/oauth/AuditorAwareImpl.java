package com.nailcase.oauth;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.NailArtistDetails;

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

		if (authentication.getPrincipal() instanceof NailArtistDetails nailArtistDetails) {
			log.debug("Returning auditor ID: {}", nailArtistDetails.getNailArtistId());
			return Optional.of(nailArtistDetails.getNailArtistId());
		} else if (authentication.getPrincipal() instanceof MemberDetails memberDetails) {
			log.debug("Returning auditor ID: {}", memberDetails.getMemberId());
			return Optional.of(memberDetails.getMemberId());
		}

		log.debug("Unknown principal type: {}", authentication.getPrincipal().getClass());
		return Optional.empty();
	}
}