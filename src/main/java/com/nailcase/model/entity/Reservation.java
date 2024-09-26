package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.ReservationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "reservations")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_id")
	private Long reservationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member customer;

	// 예약자명
	@Column(name = "customer_name")
	private String customerName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nail_artist_id")
	private NailArtist nailArtist;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "reservation_detail_id")
	private ReservationDetail reservationDetail;

	@Column(name = "cancel_reason", length = 2048)
	private String cancelReason;

	public void updateCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public void setReservationDetail(ReservationDetail reservationDetail) {
		this.reservationDetail = reservationDetail;
	}

	public boolean isConfirmable() {
		return this.reservationDetail != null && this.reservationDetail.isConfirmable();
	}

	public void confirm() {
		if (this.reservationDetail != null) {
			this.reservationDetail.confirm();
		}
	}

	public void updateNickname(String nickname) {
		this.customerName = nickname;
	}

	public boolean isStatusUpdatable(ReservationStatus status) {
		return this.reservationDetail != null && this.reservationDetail.isStatusUpdatable(status);
	}

	public void updateStatus(ReservationStatus status) {
		if (this.reservationDetail != null) {
			this.reservationDetail.updateStatus(status);
		}
	}
}