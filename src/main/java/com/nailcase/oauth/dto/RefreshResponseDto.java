package com.nailcase.oauth.dto;

import lombok.Getter;

@Getter
public class RefreshResponseDto {
	private final String message = "토큰 갱신 요청이 처리되었습니다.";
}
