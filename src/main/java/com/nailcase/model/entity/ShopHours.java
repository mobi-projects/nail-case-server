package com.nailcase.model.entity;

import java.time.LocalDateTime;

import com.nailcase.common.BaseEntity;
import com.nailcase.converter.LocalDateTimeToLocalTimeConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_hours")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopHours extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shop_hours_id;

	@Column(nullable = false)
	private Long shop_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id", referencedColumnName = "shop_id", insertable = false, updatable = false)
	private Shop shop;

	@Column(nullable = false)
	private int dayOfWeek;

	@Column(name = "open_time", nullable = false, columnDefinition = "TIME")
	@Convert(converter = LocalDateTimeToLocalTimeConverter.class)
	private LocalDateTime openTime;

	@Column(name = "close_time", nullable = false, columnDefinition = "TIME")
	@Convert(converter = LocalDateTimeToLocalTimeConverter.class)
	private LocalDateTime closeTime;
}
