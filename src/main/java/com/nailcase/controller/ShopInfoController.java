package com.nailcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}")
@RequiredArgsConstructor
public class ShopInfoController {
	@PatchMapping("/covers/{coverId}")
	public void updateCover(@PathVariable Long shopId, @PathVariable Long coverId) {
	}

	@GetMapping("/covers")
	public void listCovers(@PathVariable Long shopId) {
	}

	@GetMapping("/info")
	public void viewInfo(@PathVariable Long shopId) {
	}
}
