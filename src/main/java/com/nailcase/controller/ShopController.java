package com.nailcase.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.MemberDetails;
import com.nailcase.model.dto.ShopDto;
import com.nailcase.service.ShopService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {

	private final ShopService shopService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ShopDto.Response registerShop(
		@Valid @RequestBody ShopDto.Post shopRegisterRequest,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return shopService.registerShop(shopRegisterRequest, memberDetails.getMemberId());
	}

	@GetMapping("/{shopId}")
	public ShopDto.Response getShopById(@PathVariable Long shopId) {
		return shopService.getShop(shopId);
	}

	@GetMapping
	public void searchShop() {
	}

	@DeleteMapping("/{shopId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteShop(@PathVariable Long shopId, @AuthenticationPrincipal MemberDetails memberDetails) {
		shopService.deleteShop(shopId, memberDetails.getMemberId());
	}
}
