package com.nailcase.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.exception.codes.NailArtistErrorCode;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.dto.UserPrincipalImpl;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;

	public UserPrincipal createUserPrincipalDetails(Role role, Long userId) {
		return switch (role) {
			case MEMBER -> createUserPrincipalFromMember(userId);
			case MANAGER -> createUserPrincipalFromNailArtist(userId);
			default -> throw new BusinessException(AuthErrorCode.INVALID_USER_TYPE);
		};
	}

	private UserPrincipal createUserPrincipalFromMember(Long userId) {
		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return createUserPrincipal(member);
	}

	private UserPrincipal createUserPrincipalFromNailArtist(Long userId) {
		NailArtist nailArtist = nailArtistRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(NailArtistErrorCode.NOT_FOUND));
		return createUserPrincipal(nailArtist);
	}

	private UserPrincipal createUserPrincipal(Member member) {
		return new UserPrincipalImpl(
			member.getMemberId(),
			member.getEmail(),
			member.getNickname(),
			member.getRole()
		);
	}

	private UserPrincipal createUserPrincipal(NailArtist nailArtist) {
		return new UserPrincipalImpl(
			nailArtist.getNailArtistId(),
			nailArtist.getEmail(),
			nailArtist.getNickname(),
			nailArtist.getRole()
		);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 구현 필요
		return null;
	}
}