package com.nailcase.model.enums;

public enum ReservationStatus {
	PENDING, CANCELED, REJECTED, CONFIRMED
	// PENDING -> CANCELED
	// PENDING -> REJECTED
	// PENDING -> CONFIRMED
	// CANCELED -> X
	// REJECTED -> X
	// CONFIRMED -> X
}
