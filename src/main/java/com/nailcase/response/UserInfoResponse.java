package com.nailcase.response;

import com.nailcase.model.enums.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
	private Long shopId;
	private String shopName;
	private String profileImage;
	private Role role;
	private Long userId;
	private String nickName;
}
