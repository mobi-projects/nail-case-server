package com.nailcase.model.enums;

import java.util.Set;

public enum ReservationStatus {
	PENDING, CANCELED, REJECTED, CONFIRMED, COMPLETED;

	// PENDING -> CANCELED
	// PENDING -> REJECTED
	// PENDING -> CONFIRMED
	// CANCELED -> X
	// REJECTED -> X
	// CONFIRMED -> X
	// user( 현재 로그인된 )가 reservation의 예약자가 아닐 때 CANCEL -> X
	// user( 현재 로그인된 )가 shop의 관리자가 아닐 때 REJECT -> X
	public static Set<ReservationStatus> getNotUpdateable() {
		return Set.of(CANCELED, REJECTED, COMPLETED);
	}
}
