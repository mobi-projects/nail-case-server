package com.nailcase.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.model.dto.NailArtistDto;
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

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ShopDto.ShopRegiResponse registerShop(
		@RequestPart("shopData") String shopDataJson,
		@RequestPart(required = false) List<MultipartFile> profileImages,
		@RequestPart(required = false) List<MultipartFile> priceImages,
		@AuthenticationPrincipal Long id
	) {
		return shopService.registerShop(shopDataJson, profileImages, priceImages, id);
	}

	@GetMapping("/{shopId}")
	public ShopDto.Response getShopById(@PathVariable Long shopId, @AuthenticationPrincipal Long userId) {
		return shopService.getShop(shopId, userId);
	}

	@GetMapping("/search/{keyword}")
	public Page<ShopDto.Response> searchShop(@PathVariable String keyword, @RequestParam(defaultValue = "1") int page) {
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return shopService.searchShop(keyword, pageable);
	}

	@DeleteMapping("/{shopId}")
	public void deleteShop(
		@PathVariable Long shopId,
		@AuthenticationPrincipal Long userId
	) {
		shopService.deleteShop(shopId, userId);
	}

	@PutMapping("/{shopId}")
	public ShopDto.Response updateShop(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopDto.Post putRequest,
		@AuthenticationPrincipal Long userId
	) {
		return shopService.updateShop(shopId, putRequest);
	}

	@GetMapping("/tags")
	public List<String> getTags() {
		return shopService.getTags();
	}

	@PatchMapping("/{shopId}/overview")
	public ShopDto.Response updateOverview(
		@PathVariable Long shopId,
		@Valid @RequestBody ShopDto.Patch patchRequest,
		@AuthenticationPrincipal Long userId
	) {
		return shopService.updateOverview(shopId, patchRequest, userId);
	}

	@PostMapping("/{shopId}/image")
	@ResponseStatus(HttpStatus.CREATED)
	public CompletableFuture<String> uploadImage(
		@PathVariable Long shopId,
		@RequestParam("file") MultipartFile file,
		@AuthenticationPrincipal Long userId
	) {
		return shopService.uploadImage(shopId, file, userId);
	}

	@DeleteMapping("/image/{imageId}")
	public void deleteImage(
		@PathVariable Long imageId,
		@AuthenticationPrincipal Long userId
	) {
		shopService.deleteImage(imageId, userId);
	}

	@GetMapping("/{shopId}/manager/list")
	public List<NailArtistDto.ListResponse> listShopNailArtist(
		@PathVariable Long shopId
	) {
		return shopService.listShopNailArtist(shopId);
	}

	@PostMapping("/{shopId}/toggle-like")
	public boolean toggleLikeShop(
		@PathVariable Long shopId,
		@AuthenticationPrincipal Long userId) {

		log.info("toggleLike shop: {} for shopId: {}", userId, shopId);
		return shopService.toggleLike(shopId, userId);
	}

}
