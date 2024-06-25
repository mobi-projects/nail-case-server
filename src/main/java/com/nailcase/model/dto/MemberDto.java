package com.nailcase.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nailcase.model.entity.Member;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.SocialType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDto {
	private Long memberId;
	private String name;
	private String email;
	private String profileImgUrl;
	private Role role;
	private String socialId;
	private SocialType socialType;

	public static MemberDto fromEntity(Member member) {
		MemberDto dto = new MemberDto();
		dto.setMemberId(member.getMemberId());
		dto.setName(member.getName());
		dto.setEmail(member.getEmail());
		dto.setProfileImgUrl(member.getProfileImgUrl());
		dto.setRole(member.getRole());
		dto.setSocialId(member.getSocialId());
		dto.setSocialType(member.getSocialType());
		return dto;
	}
}