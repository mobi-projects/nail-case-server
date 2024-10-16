package com.nailcase.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.ShopInfoDto;
import com.nailcase.service.ShopInfoService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/info")
@RequiredArgsConstructor
public class ShopInfoController {

	private final ShopInfoService shopInfoService;

	@GetMapping
	@Operation(summary = "샵의 상세 정보를 제공합니다.", description = "주소, 안내, 가격 ")
	public ShopInfoDto.Response getShopInfo(@PathVariable Long shopId) {
		return shopInfoService.getShopInfo(shopId);
	}

	@PatchMapping("/address")
	@Operation(summary = "샵의 주소 정보를 수정합니다.", description = "'시도 시군구' | '경도,위도' 주의해주세요.")
	public ShopInfoDto.Address updateAddress(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopInfoDto.Address requestAddress,
		@AuthenticationPrincipal Long userId
	) {
		return shopInfoService.updateAddress(shopId, requestAddress, userId);
	}

	@PatchMapping
	@Operation(summary = "샵의 가격 정보를 수정합니다.")
	public ShopInfoDto.Info updateInfo(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopInfoDto.Info requestInfo,
		@AuthenticationPrincipal Long userId
	) {
		return shopInfoService.updateInfo(shopId, requestInfo, userId);
	}

	@PatchMapping("/price")
	@Operation(summary = "샵의 가격 정보를 수정합니다.")
	public ShopInfoDto.PriceResponse updatePrice(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopInfoDto.Price requestPrice,
		@AuthenticationPrincipal Long userId
	) {
		return shopInfoService.updatePrice(shopId, requestPrice, userId);
	}

}
