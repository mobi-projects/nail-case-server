package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "shop_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopInfo extends BaseEntity {

	@Id
	@Column(name = "shop_info_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopInfoId;

	@Column(name = "shop_id")
	private Long shopId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id", nullable = false, insertable = false, updatable = false)
	private Shop shop;

	@Setter
	@Column(name = "point")
	private String point;

	@Column(name = "parking_lot_cnt")
	private Integer parkingLotCnt;

	@Column(name = "available_cnt")
	private Integer availableCnt;

	@Column(name = "info")
	private String info;

	@Column(name = "price")
	private String price;

	@OneToOne(mappedBy = "shopInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private PriceImage priceImage;
}
