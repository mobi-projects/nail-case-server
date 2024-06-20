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

import com.nailcase.model.dto.MemberDetails;
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

	// 이미지만 업로드하는 API
	@PostMapping("/images")
	@ResponseStatus(HttpStatus.CREATED)
	public List<MonthlyArtImageDto> uploadImages(@RequestParam("files") List<MultipartFile> files,
		@AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long shopId) {
		Long memberId = memberDetails.getMemberId();
		log.info("Uploading images for shopId: {}", shopId);
		return monthlyArtService.uploadImages(files, memberId);
	}

	@PostMapping
	public MonthlyArtDto.Response registerMonthlyArt(@PathVariable Long shopId,
		@RequestBody MonthlyArtDto.Request monthlyArtRequest) {
		log.info("Registering new monthly art for shopId: {}", shopId);
		return monthlyArtService.registerMonthlyArt(shopId, monthlyArtRequest);
	}

	@PutMapping("/{monthlyArtId}")
	public MonthlyArtDto.Response updateMonthlyArt(@PathVariable Long shopId, @PathVariable Long monthlyArtId,
		@RequestBody MonthlyArtDto.Request monthlyArtRequest, @AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Updating monthly art: {} for shopId: {}", monthlyArtId, shopId);
		Long memberId = memberDetails.getMemberId();
		return monthlyArtService.updateMonthlyArt(shopId, monthlyArtId, monthlyArtRequest, memberId);
	}

	@GetMapping
	public List<MonthlyArtDto.Response> listMonthlyArt(@PathVariable Long shopId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Listing all monthly arts for shopId: {}", shopId);
		Long memberId = memberDetails.getMemberId();
		return monthlyArtService.listMonthlyArts(shopId, memberId);
	}

	@GetMapping("/{monthlyArtId}")
	public MonthlyArtDto.Response viewMonthlyArt(@PathVariable Long shopId, @PathVariable Long monthlyArtId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Viewing monthly art: {} for shopId: {}", monthlyArtId, shopId);
		Long memberId = memberDetails.getMemberId();
		return monthlyArtService.viewMonthlyArt(shopId, monthlyArtId, memberId);
	}

	@DeleteMapping("/{monthlyArtId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMonthlyArt(@PathVariable Long shopId, @PathVariable Long monthlyArtId) {
		log.info("Deleting monthly art: {} for shopId: {}", monthlyArtId, shopId);
		monthlyArtService.deleteMonthlyArt(shopId, monthlyArtId);
	}

	@PostMapping("/{monthlyArtId}/like")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void likeMonthlyArt(@PathVariable Long shopId, @PathVariable Long monthlyArtId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Liking monthly art: {} for shopId: {}", monthlyArtId, shopId);
		Long memberId = memberDetails.getMemberId();
		monthlyArtService.likeMonthlyArt(monthlyArtId, memberId);
	}

	@PostMapping("/{monthlyArtId}/unlike")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unlikeMonthlyArt(@PathVariable Long shopId, @PathVariable Long monthlyArtId,
		@AuthenticationPrincipal MemberDetails memberDetails) {
		log.info("Unliking monthly art: {} for shopId: {}", monthlyArtId, shopId);
		Long memberId = memberDetails.getMemberId();
		monthlyArtService.unlikeMonthlyArt(monthlyArtId, memberId);
	}
}