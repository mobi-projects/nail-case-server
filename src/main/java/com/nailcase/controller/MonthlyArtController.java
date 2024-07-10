package com.nailcase.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.model.dto.MonthlyArtDto;
import com.nailcase.model.dto.MonthlyArtImageDto;
import com.nailcase.service.MonthlyArtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shops/{shopId}/monthly-art")
@RequiredArgsConstructor
public class MonthlyArtController {
	private final MonthlyArtService monthlyArtService;

	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public List<MonthlyArtImageDto> uploadImages(
		@RequestParam("files") List<MultipartFile> files,
		@AuthenticationPrincipal Long userId,
		@PathVariable Long shopId) {
		log.info("Uploading images for shopId: {} by userId: {}", shopId, userId);
		return monthlyArtService.uploadImages(files, userId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MonthlyArtDto.Response registerMonthlyArt(
		@PathVariable Long shopId,
		@RequestBody MonthlyArtDto.Request monthlyArtRequest,
		@AuthenticationPrincipal Long userId) {
		log.info("Registering new monthly art for shopId: {} by userId: {}", shopId, userId);
		return monthlyArtService.registerMonthlyArt(shopId, monthlyArtRequest);
	}

	@PutMapping("/{monthlyArtId}")
	public MonthlyArtDto.Response updateMonthlyArt(
		@PathVariable Long shopId,
		@PathVariable Long monthlyArtId,
		@RequestBody MonthlyArtDto.Request monthlyArtRequest,
		@AuthenticationPrincipal Long userId) {
		log.info("Updating monthly art: {} for shopId: {} by userId: {}", monthlyArtId, shopId, userId);
		return monthlyArtService.updateMonthlyArt(shopId, monthlyArtId, monthlyArtRequest, userId);
	}

	@GetMapping
	public List<MonthlyArtDto.Response> listMonthlyArt(
		@PathVariable Long shopId,
		@AuthenticationPrincipal Long userId) {
		log.info("Listing all monthly arts for shopId: {} by userId: {}", shopId, userId);
		return monthlyArtService.listMonthlyArts(shopId, userId);
	}

	@GetMapping("/{monthlyArtId}")
	public MonthlyArtDto.Response viewMonthlyArt(
		@PathVariable Long shopId,
		@PathVariable Long monthlyArtId,
		@AuthenticationPrincipal Long userId) {
		log.info("Viewing monthly art: {} for shopId: {} by userId: {}", monthlyArtId, shopId, userId);
		return monthlyArtService.viewMonthlyArt(shopId, monthlyArtId, userId);
	}

	@DeleteMapping("/{monthlyArtId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMonthlyArt(
		@PathVariable Long shopId,
		@PathVariable Long monthlyArtId,
		@AuthenticationPrincipal Long userId) {
		log.info("Deleting monthly art: {} for shopId: {} by userId: {}", monthlyArtId, shopId, userId);
		monthlyArtService.deleteMonthlyArt(shopId, monthlyArtId, userId);
	}

	@PostMapping("/{monthlyArtId}/like")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void likeMonthlyArt(
		@PathVariable Long shopId,
		@PathVariable Long monthlyArtId,
		@AuthenticationPrincipal Long userId) {
		log.info("Liking monthly art: {} for shopId: {} by userId: {}", monthlyArtId, shopId, userId);
		monthlyArtService.likeMonthlyArt(monthlyArtId, userId);
	}

	@PostMapping("/{monthlyArtId}/unlike")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unlikeMonthlyArt(
		@PathVariable Long shopId,
		@PathVariable Long monthlyArtId,
		@AuthenticationPrincipal Long userId) {
		log.info("Unliking monthly art: {} for shopId: {} by userId: {}", monthlyArtId, shopId, userId);
		monthlyArtService.unlikeMonthlyArt(monthlyArtId, userId);
	}
}