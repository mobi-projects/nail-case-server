package com.nailcase.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.annotation.AuthenticatedManagerUser;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.jwt.JwtService;
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
	private final JwtService jwtService;

	@Value("${spring.data.web.pageable.default-page-size}")
	private int pageSize;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ShopDto.Response registerShop(
		@Valid @RequestBody ShopDto.Post postDto,
		@AuthenticatedManagerUser Long managerId
	) {
		return shopService.registerShop(postDto, managerId);
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
	public void deleteShop(
		@PathVariable Long shopId,
		@AuthenticatedManagerUser Long managerId
	) {
		shopService.deleteShop(shopId, managerId);
	}

	@PutMapping("/{shopId}")
	public ShopDto.Response updateShop(@PathVariable Long shopId, @Valid @RequestBody ShopDto.Post putRequest) {
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
		@AuthenticatedManagerUser Long managerId
	) {
		return shopService.updateOverview(shopId, patchRequest, managerId);
	}

	@PostMapping("/{shopId}/image")
	@ResponseStatus(HttpStatus.CREATED)
	public String uploadImage(
		@PathVariable Long shopId,
		@RequestParam("file") MultipartFile file,
		@AuthenticatedManagerUser Long managerId
	) {
		return shopService.uploadImage(shopId, file, managerId);
	}

	@DeleteMapping("/image/{imageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteImage(
		@PathVariable Long imageId,
		@RequestHeader("Authorization") String token
	) {
		Long userId = jwtService.extractUserId(token)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.TOKEN_INVALID));
		shopService.deleteImage(imageId, userId);
	}
}
