package com.nailcase.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.RemoveOption;
import com.nailcase.model.enums.ReservationStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "reservation_details")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationDetail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_detail_id")
	private Long reservationDetailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nail_artist_id")
	private NailArtist nailArtist;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id")
	private Reservation reservation;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "reservationDetail")
	private List<Treatment> treatmentList = new ArrayList<>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "reservationDetail")
	private List<Condition> conditionList = new ArrayList<>();

	@Column(name = "start_time", nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime endTime;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private ReservationStatus status = ReservationStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "remove", nullable = false, length = 16)
	private RemoveOption remove;

	private boolean extend;

	public boolean isStatusUpdatable(ReservationStatus status) {
		if (this.status.equals(ReservationStatus.REJECTED)) {
			return false;
		}
		if (this.status.equals(ReservationStatus.CANCELED)) {
			return false;
		}
		if (this.status.equals(ReservationStatus.CONFIRMED) && status.equals(ReservationStatus.PENDING)) {
			return false;
		}
		return true;
	}

	public void updateStatus(ReservationStatus status) {
		this.status = status;
	}

	public void associateDown(Reservation reservation) {
		this.reservation = reservation;
		this.conditionList.forEach(condition -> condition.associateDown(this));
		this.treatmentList.forEach(treatment -> treatment.associateDown(this));
	}

	public void updateArtist(NailArtist nailArtist) {
		if (nailArtist != null) {
			this.nailArtist = nailArtist;
		}
	}
}
