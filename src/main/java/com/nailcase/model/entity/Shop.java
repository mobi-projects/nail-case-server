package com.nailcase.model.entity;

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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
	@Column(name = "ownerId", nullable = false, insertable = false, updatable = false)
	private Long ownerId;

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

	@OneToOne(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private ShopInfo shopInfo;

	@OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	Set<ShopHour> shopHours;
}
