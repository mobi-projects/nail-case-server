package com.nailcase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shops/{shopId}/reservations")
@RequiredArgsConstructor
public class ReservationController {
	@PostMapping
	public void registerReservation(@PathVariable Long shopId) {
	}

	@PatchMapping("/{reservationId}")
	public void updateReservation(@PathVariable Long shopId, @PathVariable Long reservationId) {
	}

	@GetMapping
	public void listReservation(@PathVariable Long shopId) {
	}

	@GetMapping("/{reservationId}")
	public void viewReservation(@PathVariable Long shopId, @PathVariable Long reservationId) {
	}
}
