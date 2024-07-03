package com.nailcase.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.ShopInfoDto;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/info")
@RequiredArgsConstructor
public class ShopInfoController {

	@GetMapping
	@Operation(summary = "샵의 상세 정보를 제공합니다.", description = "주소, 안내, 가격 ")
	public ShopInfoDto.Response getShopInfo(@PathVariable Long shopId) {
		return new ShopInfoDto.Response();
	}

	@PatchMapping("/address")
	@Operation(summary = "샵의 주소 정보를 수정합니다.", description = "'시도 시군구' | '경도,위도' 주의해주세요.")
	public ShopInfoDto.Address updateAddress(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopInfoDto.Address requestAddress,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return new ShopInfoDto.Address();
	}

	@PatchMapping("/info")
	@Operation(summary = "샵의 가격 정보를 수정합니다.")
	public ShopInfoDto.Info updateInfo(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopInfoDto.Info requestInfo,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return new ShopInfoDto.Info();
	}

	@PatchMapping("/price")
	@Operation(summary = "샵의 가격 정보를 수정합니다.")
	public ShopInfoDto.Price updatePrice(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopInfoDto.Price requestPrice,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return new ShopInfoDto.Price();
	}

}
