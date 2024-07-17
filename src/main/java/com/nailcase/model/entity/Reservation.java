package com.nailcase.model.entity;

import java.util.LinkedHashSet;
import java.util.Set;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

	// 외부 방문 고객인 경우?
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nail_artist_id")
	private NailArtist nailArtist;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "reservation")
	private Set<ReservationDetail> reservationDetailList = new LinkedHashSet<>();

	// 순환 참조를 막기 위해 한 방향으로만 관계를 맺음
	public void associateDown() {
		this.reservationDetailList.forEach(reservationDetail -> reservationDetail.associateDown(this));
	}

	public boolean isAccompanied() {
		return reservationDetailList.size() > 1;
	}

	public boolean isConfirmable() {
		return this.reservationDetailList.stream().allMatch(ReservationDetail::isConfirmable);
	}

	public void confirm() {
		this.reservationDetailList.forEach(ReservationDetail::confirm);
	}

	public boolean isStatusUpdatable(ReservationStatus status) {
		return this.reservationDetailList.stream()
			.allMatch(reservationDetail -> reservationDetail.isStatusUpdatable(status));
	}

	public void updateStatus(ReservationStatus status) {
		this.reservationDetailList.forEach(reservationDetail -> reservationDetail.updateStatus(status));
	}
}