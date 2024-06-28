package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;

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
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Table(name = "shop_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopInfo extends BaseEntity {

	@Id
	@Column(name = "shop_info_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopInfoId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id", nullable = false)
	private Shop shop;

	// TODO geometry 추가하고 geometry로 바꾸기
	@Column(name = "geometry")
	private String geometry;

	@Column(name = "parking_lot_cnt")
	private Integer parkingLotCnt;

	@Column(name = "accompany_cnt")
	private Integer accompanyCnt;

	@Column(name = "info")
	private String info;

	@Column(name = "price")
	private String price;

	// TODO img
	@Column(name = "img")
	private String img;
}
