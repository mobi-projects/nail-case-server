package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nailcase.common.BaseEntity;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "shops")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop extends BaseEntity {

	@Id
	@Column(name = "shop_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopId;

	// @Setter
	// @Column(name = "owner_id", nullable = false, insertable = false, updatable = false)
	// private Long ownerId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id", referencedColumnName = "member_id")
	private Member member;

	@Column(name = "shop_name", nullable = false, length = 128)
	private String shopName;

	@Column(name = "phone", nullable = false, length = 16)
	private String phone;

	@Column(name = "overview", length = 2048)
	private String overview;

	@Column(name = "address", length = 128)
	private String address;

	@Builder.Default
	@Column(name = "available_seat")
	private Integer availableSeats = 0;

	@OneToOne(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private ShopInfo shopInfo;

	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ShopHour> shopHours;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private List<NailArtist> nailArtistList = new ArrayList<>();

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
}
