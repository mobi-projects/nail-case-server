package com.nailcase.oauth2;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nailcase.model.dto.MemberDetails;

@ExtendWith(MockitoExtension.class)
public class AuditorAwareImplTest {

	@InjectMocks
	private AuditorAwareImpl auditorAware;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private Authentication authentication;

	@BeforeEach
	public void setUp() {
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	@DisplayName("인증된 사용자의 ID를 반환하는 테스트")
	public void getCurrentAuditorWithAuthenticatedUser() {
		// Given
		MemberDetails member = mock(MemberDetails.class);
		when(member.getMemberId()).thenReturn(1L);
		when(authentication.isAuthenticated()).thenReturn(true);
		when(authentication.getPrincipal()).thenReturn(member);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		// When
		Optional<Long> currentAuditor = auditorAware.getCurrentAuditor();

		// Then
		assertThat(currentAuditor).isPresent();
		assertThat(currentAuditor.get()).isEqualTo(1L);
	}

	@Test
	@DisplayName("인증되지 않은 사용자는 빈 Optional을 반환하는 테스트")
	public void getCurrentAuditorWithUnauthenticatedUser() {
		// Given
		when(securityContext.getAuthentication()).thenReturn(null);

		// When
		Optional<Long> currentAuditor = auditorAware.getCurrentAuditor();

		// Then
		assertThat(currentAuditor).isEmpty();
	}

	@Test
	@DisplayName("익명 사용자는 빈 Optional을 반환하는 테스트")
	public void getCurrentAuditorWithAnonymousUser() {
		// Given
		when(authentication.isAuthenticated()).thenReturn(false);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		// When
		Optional<Long> currentAuditor = auditorAware.getCurrentAuditor();

		// Then
		assertThat(currentAuditor).isEmpty();
	}
}
