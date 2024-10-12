package com.nailcase.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationDetailProjection {
	private Long reservationDetailId;
	private Long shopId;
	private String shopName;
	private Long customerId;

	public ReservationDetailProjection(Long reservationDetailId, Long shopId, String shopName, Long customerId) {
		this.reservationDetailId = reservationDetailId;
		this.shopId = shopId;
		this.shopName = shopName;
		this.customerId = customerId;
	}

}