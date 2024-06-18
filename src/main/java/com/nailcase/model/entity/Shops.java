package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.nailcase.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "shops")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Shops extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shop_id")
	private Long shopId;

	private Integer availableSeats;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private List<NailArtists> nailArtistList = new ArrayList<>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private List<Reservation> reservationList = new ArrayList<>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private List<ReservationDetail> reservationDetailList = new ArrayList<>();

	public void associateDown() {
		this.nailArtistList.forEach(nailArtist -> nailArtist.associateDown(this));
	}

	public void minusAvailableSeats() {
		this.availableSeats--;
	}

	public void plusAvailableSeats() {
		this.availableSeats++;
	}

	public void associateUp(NailArtists nailArtist) {
		if (nailArtist != null) {
			this.nailArtistList.add(nailArtist);
		}
	}
}
