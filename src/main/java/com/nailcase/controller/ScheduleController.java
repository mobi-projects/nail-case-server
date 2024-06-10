package com.nailcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/schedule")
@RequiredArgsConstructor
public class ScheduleController {
	@GetMapping
	public void listSchedule(@PathVariable String shopId) {
	}
}
