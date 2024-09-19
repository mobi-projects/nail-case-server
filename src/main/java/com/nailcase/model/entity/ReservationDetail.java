package com.nailcase.model.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
import jakarta.persistence.OneToOne;
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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "treatment_id")
	private Treatment treatment;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "reservationDetail")
	private Set<Condition> conditionList = new HashSet<>();

	@Column(name = "start_time", nullable = false)
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime startTime;

	@Column(name = "end_time")
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime endTime;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private ReservationStatus status = ReservationStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "remove", nullable = false, length = 16)
	private RemoveOption remove;

	@Column(name = "price")
	private String price;

	@OneToOne(mappedBy = "reservationDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Review review;

	private boolean extend;

	public void updatePrice(String price) {
		this.price = price;
	}

	public boolean isStatusUpdatable(ReservationStatus status) {
		if (ReservationStatus.getNotUpdateable().contains(this.status)) {
			return false;
		}
		if (this.status.equals(ReservationStatus.CONFIRMED) && status.equals(ReservationStatus.PENDING)) {
			return false;
		}
		return this.status.equals(ReservationStatus.PENDING);
	}

	public void updateStatus(ReservationStatus status) {
		this.status = status;
	}

	public void updateArtist(NailArtist nailArtist) {
		if (nailArtist != null) {
			this.nailArtist = nailArtist;
		}
	}

	public void setReview(Review review) {
		this.review = review;
		if (review != null && review.getReservationDetail() != this) {
			review.setReservationDetail(this);
		}
	}

	public void updateReservationTime(LocalDateTime newEndTime) {
		if (newEndTime != null) {
			this.endTime = newEndTime;
		}
	}

	public boolean isReservationTimeUpdatable(LocalDateTime newEndTime) {
		System.out.println("this.startTime = " + this.startTime);
		LocalDateTime now = LocalDateTime.now();
		return newEndTime.isAfter(this.startTime) && newEndTime.isAfter(now);
	}

	public boolean isConfirmable() {
		if (this.endTime == null) {
			return false;
		}
		return this.status == ReservationStatus.PENDING;
	}

	public void confirm() {
		this.status = ReservationStatus.CONFIRMED;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof ReservationDetail))
			return false;
		ReservationDetail that = (ReservationDetail)o;
		return Objects.equals(getReservationDetailId(), that.getReservationDetailId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getReservationDetailId());
	}
}