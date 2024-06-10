package com.nailcase.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopController {
	@PostMapping
	public void registerShop() {
	}

	@GetMapping
	public void searchShop() {
	}

	@DeleteMapping("/{shopId}")
	public void deleteShop(@PathVariable String shopId) {
	}
}
