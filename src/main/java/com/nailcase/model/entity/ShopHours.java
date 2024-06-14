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
	@Column(name = "shop_hours_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopHoursId;

	@Column(name = "shop_id", nullable = false)
	private Long shopId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id", referencedColumnName = "shop_id", insertable = false, updatable = false)
	private Shop shop;

	@Column(name = "day_of_week", nullable = false)
	private int dayOfWeek;

	@Column(name = "open_time", nullable = false, columnDefinition = "TIME")
	@Convert(converter = LocalDateTimeToLocalTimeConverter.class)
	private LocalDateTime openTime;

	@Column(name = "close_time", nullable = false, columnDefinition = "TIME")
	@Convert(converter = LocalDateTimeToLocalTimeConverter.class)
	private LocalDateTime closeTime;
}
