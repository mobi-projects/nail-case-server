package com.nailcase.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UserErrorCode;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.NailArtist;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.NailArtistRepository;
import com.nailcase.response.UserInfoResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final MemberRepository memberRepository;
	private final NailArtistRepository nailArtistRepository;

	public UserInfoResponse getUserInfo(Long userId, Role role) {
		return switch (role) {
			case MEMBER -> getMemberInfo(userId);
			case MANAGER -> getNailArtistInfo(userId);
			default -> throw new BusinessException(UserErrorCode.INVALID_USER_INPUT);
		};
	}

	private UserInfoResponse getMemberInfo(Long userId) {
		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return UserInfoResponse.builder()
			.userId(member.getMemberId())
			.profileImage(member.getProfileImgUrl())
			.nickName(member.getNickname())
			.role(member.getRole())
			.build();
	}

	private UserInfoResponse getNailArtistInfo(Long userId) {
		NailArtist nailArtist = nailArtistRepository.findByIdWithShop(userId)
			.orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
		return UserInfoResponse.builder()
			.userId(nailArtist.getNailArtistId())
			.shopId(Optional.ofNullable(nailArtist.getShop()).map(Shop::getShopId).orElse(null))
			.shopName(Optional.ofNullable(nailArtist.getShop()).map(Shop::getShopName).orElse(null))
			.profileImage(nailArtist.getProfileImgUrl())
			.role(nailArtist.getRole())
			.nickName(nailArtist.getNickname())
			.build();
	}

	public boolean isValidUserRole(Long userId, Role role) {
		return switch (role) {
			case MEMBER -> memberRepository.findById(userId).isPresent();
			case MANAGER -> nailArtistRepository.findById(userId).isPresent();
			default -> false;
		};
	}
}