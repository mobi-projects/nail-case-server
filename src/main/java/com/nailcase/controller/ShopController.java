package com.nailcase.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailcase.model.dto.ShopRegisterDto;
import com.nailcase.oauth2.CustomOAuth2User;
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
	public ShopRegisterDto.Response registerShop(
		@Valid @RequestBody ShopRegisterDto.Request shopRegisterRequest,
		@AuthenticationPrincipal CustomOAuth2User customOAuth2User
	) {
		return shopService.registerShop(shopRegisterRequest, customOAuth2User.getMemberId());
	}

	@GetMapping("/{shopId}")
	public void getShopById(@PathVariable Long shopId) {
		
	}

	@GetMapping
	public void searchShop() {
	}

	@DeleteMapping("/{shopId}")
	public void deleteShop(@PathVariable String shopId) {
	}
}
