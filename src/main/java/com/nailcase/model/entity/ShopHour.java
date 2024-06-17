package com.nailcase.model.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Table(name = "shop_hours")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopHour extends BaseEntity {

	@Id
	@Column(name = "shop_hours_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long shopHoursId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id", nullable = false)
	private Shop shop;

	@Column(name = "day_of_week", nullable = false)
	private int dayOfWeek;

	@Builder.Default
	@Column(name = "is_open")
	private Boolean isOpen = false;

	@Builder.Default
	@Column(name = "open_time", nullable = false, columnDefinition = "TIME")
	@Convert(converter = LocalDateTimeToLocalTimeConverter.class)
	private LocalDateTime openTime = LocalDateTime.now().with(LocalTime.of(9, 0));

	@Builder.Default
	@Column(name = "close_time", nullable = false, columnDefinition = "TIME")
	@Convert(converter = LocalDateTimeToLocalTimeConverter.class)
	private LocalDateTime closeTime = LocalDateTime.now().with(LocalTime.of(18, 0));
}
