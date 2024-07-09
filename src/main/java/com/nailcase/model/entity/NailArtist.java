package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.nailcase.model.enums.ManagerRole;
import com.nailcase.model.enums.SocialType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "nail_artists")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NailArtist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nail_artist_id")
	private Long nailArtistId;

	@Column(name = "name", length = 128)
	private String name;

	@Column(name = "email", length = 128)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "social_type")
	private SocialType socialType; // KAKAO, NAVER, FACEBOOK*/

	@Column(name = "social_id")
	private String socialId;

	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "manager_role", nullable = false)
	private ManagerRole managerRole;

	@Column(name = "profile_img_url", length = 128)
	private String profileImgUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "nailArtist")
	private List<Reservation> timeTableList = new ArrayList<>();

	@Builder.Default
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "nailArtist")
	private List<ReservationDetail> reservationDetailList = new ArrayList<>();

	public void associateDown(Shop shop) {
		if (shop != null) {
			this.shop = shop;
		}
	}
}