package com.nailcase.model.enums;

public enum ReservationStatus {
	PENDING, CANCELED, REJECTED, CONFIRMED
	// PENDING -> CANCELED
	// PENDING -> REJECTED
	// PENDING -> CONFIRMED
	// CANCELED -> X
	// REJECTED -> X
	// CONFIRMED -> X
	// user( 현재 로그인된 )가 reservation의 예약자가 아닐 때 CANCEL -> X
	// user( 현재 로그인된 )가 shop의 관리자가 아닐 때 REJECT -> X
}
