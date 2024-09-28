package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.HashSet;
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
import jakarta.persistence.Version;
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

	@Builder.Default
	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
	private Set<NailArtist> nailArtists = new HashSet<>();

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

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "shop_info_id")
	private ShopInfo shopInfo;

	@Builder.Default
	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<WorkHour> workHours = new ArrayList<>();

	@Builder.Default
	@OrderBy("sortOrder asc")
	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TagMapping> tags = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ShopImage> shopImages = new ArrayList<>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private Set<Reservation> reservationList = new HashSet<>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "shop")
	private Set<ReservationDetail> reservationDetailList = new HashSet<>();

	@Builder.Default
	@Column(name = "likes", nullable = false)
	@ColumnDefault("0")
	private Long likes = 0L;

	@Builder.Default
	@Version
	@Column(name = "version", nullable = false)
	@ColumnDefault("0")
	private Long version = 0L;

	@Builder.Default
	@OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PriceImage> priceImages = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ShopLikedMember> likedMembers = new HashSet<>();

	public void addLikedMember(Member member) {
		ShopLikedMember shopLikedMember = new ShopLikedMember(this, member);
		this.likedMembers.add(shopLikedMember);
		this.likes++;
	}

	public void removeLikedMember(Member member) {
		this.likedMembers.removeIf(liked -> liked.getMember().equals(member));
		if (this.likes > 0) {
			this.likes--;
		}
	}

	public boolean isLikedByMember(Member member) {
		return this.likedMembers.stream()
			.anyMatch(liked -> liked.getMember().equals(member));
	}

	public void addPriceImage(PriceImage priceImage) {
		priceImages.add(priceImage);
		priceImage.setShop(this);
	}

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
	}

	public void addShopImage(ShopImage shopImage) {
		this.shopImages.add(shopImage);
		shopImage.setShop(this);
	}

	public void addWorkHour(WorkHour workHour) {
		this.workHours.add(workHour);
		workHour.setShop(this);
	}

	// 객체 받아서 직접 확인하는 메소드
	public boolean hasNailArtist(NailArtist nailArtist) {
		return this.nailArtists.contains(nailArtist) ||
			(this.nailArtist != null && this.nailArtist.equals(nailArtist));
	}

	// id만 받아서 확인하는 메소드
	public boolean hasNailArtist(Long nailArtistId) {
		return this.nailArtists.stream()
			.anyMatch(artist -> artist.getNailArtistId().equals(nailArtistId)) ||
			(this.nailArtist != null && this.nailArtist.getNailArtistId().equals(nailArtistId));
	}

	public void updateShopInfo(ShopInfo shopInfo) {
		this.shopInfo = shopInfo;
	}

}
