package com.nailcase.jwt;

import static com.nailcase.model.enums.Role.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.NailArtistErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.dto.UserPrincipalDetails;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;

	public UserPrincipalDetails createUserPrincipalDetails(Role role, Long userId) {
		UserPrincipal userPrincipal;
		if (role == MEMBER) {
			userPrincipal = memberRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		} else if (role == MANAGER) {
			userPrincipal = nailArtistRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(NailArtistErrorCode.NOT_FOUND));
		} else {
			throw new BusinessException(AuthErrorCode.INVALID_USER_TYPE);
		}
		return UserPrincipalDetails.from(userPrincipal);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}
}
