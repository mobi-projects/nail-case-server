package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.ColumnDefault;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.dto.ShopDto;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", referencedColumnName = "nail_artist_id")
	private NailArtist nailArtist;

	@Column(name = "shop_name", nullable = false, length = 128)
	private String shopName;

	@Column(name = "phone", nullable = false, length = 16)
	private String phone;

	@Setter
	@Column(name = "overview", length = 2048)
	private String overview;

	@Setter
	@Column(name = "address", length = 128)
	private String address;

	@Builder.Default
	@Column(name = "available_seat")
	private Integer availableSeats = 0;

	@OneToOne(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private ShopInfo shopInfo;

	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<WorkHour> workHours;

	@OrderBy("sortOrder asc")
	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TagMapping> tags;

	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
	private Set<ShopImage> shopImages;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private List<Reservation> reservationList = new ArrayList<>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private List<ReservationDetail> reservationDetailList = new ArrayList<>();

	@Builder.Default
	@Column(name = "likes", nullable = false)
	@ColumnDefault("0")
	private Long likes = 0L;

	public void incrementLikes() {
		this.likes += 1;
	}

	public void decrementLikes() {
		if (this.likes > 0) {
			this.likes -= 1;
		}
	}

	public void minusAvailableSeats() {
		this.availableSeats--;
	}

	public void plusAvailableSeats() {
		this.availableSeats++;
	}

	public void update(ShopDto.Post dto) {
		this.shopName = dto.getShopName();
		this.phone = dto.getPhone();
		this.availableSeats = dto.getAvailableSeats();
	}
}
