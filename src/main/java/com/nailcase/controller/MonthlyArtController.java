package com.nailcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/monthly-art")
@RequiredArgsConstructor
public class MonthlyArtController {
	@PostMapping
	public void registerMonthlyArt(@PathVariable Long shopId) {
	}

	@GetMapping
	public void listMonthlyArt(@PathVariable Long shopId) {
	}

	@GetMapping("/{monthlyArtId}")
	public void viewMonthlyArt(@PathVariable Long shopId, @PathVariable Long monthlyArtId) {
	}
}
