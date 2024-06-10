package com.nailcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/hours")
@RequiredArgsConstructor
public class WorkHourController {
	@PutMapping
	public void updateWorkHour(@PathVariable Long shopId) {
	}

	@GetMapping
	public void viewWorkHour(@PathVariable Long shopId) {
	}
}
