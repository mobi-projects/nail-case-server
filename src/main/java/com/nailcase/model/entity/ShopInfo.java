package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Setter
	@Column(name = "point")
	private String point;

	@Setter
	@Column(name = "parking_lot_cnt")
	private Integer parkingLotCnt;

	@Setter
	@Column(name = "available_cnt")
	private Integer availableCnt;

	@Setter
	@Column(name = "info")
	private String info;

	@Setter
	@Column(name = "price")
	private String price;

}

