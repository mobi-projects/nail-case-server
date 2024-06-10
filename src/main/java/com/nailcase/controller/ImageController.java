package com.nailcase.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
	@PostMapping("/covers")
	public void uploadShopCoverImage() {
	}

	@PostMapping("/profile")
	public void uploadProfileImage() {
	}

	@PostMapping("/monthly-nail-arts")
	public void uploadImage() {
	}

	@PostMapping("/review")
	public void uploadReviewImage() {
	}
}
