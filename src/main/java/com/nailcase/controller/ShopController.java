package com.nailcase.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@Value("${spring.data.web.pageable.default-page-size}")
	private int pageSize;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ShopDto.Response registerShop(
		@Valid @RequestBody ShopDto.Post postDto,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return shopService.registerShop(postDto, memberDetails.getMemberId());
	}

	@GetMapping("/{shopId}")
	public ShopDto.Response getShopById(@PathVariable Long shopId) {
		return shopService.getShop(shopId);
	}

	@GetMapping("/search/{keyword}")
	public Page<ShopDto.Response> searchShop(@PathVariable String keyword, @RequestParam(defaultValue = "1") int page) {
		Pageable pageable = PageRequest.of(page - 1, pageSize);

		return shopService.searchShop(keyword, pageable);
	}

	@DeleteMapping("/{shopId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteShop(@PathVariable Long shopId, @AuthenticationPrincipal MemberDetails memberDetails) {
		shopService.deleteShop(shopId, memberDetails.getMemberId());
	}

	@PutMapping("/{shopId}")
	public ShopDto.Response updateShop(@PathVariable Long shopId, @Valid @RequestBody ShopDto.Post putRequest) {
		return shopService.updateShop(shopId, putRequest);
	}

	@GetMapping("/tags")
	public List<String> getTags() {
		return shopService.getTags();
	}

	@PatchMapping("/overview/{shopId}")
	public ShopDto.Response updateOverview(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopDto.Patch patchRequest,
		@AuthenticationPrincipal MemberDetails memberDetails
	) {
		return shopService.updateOverview(shopId, patchRequest, memberDetails.getMemberId());
	}
}
