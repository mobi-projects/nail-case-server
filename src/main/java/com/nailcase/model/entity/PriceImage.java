package com.nailcase.model.entity;

import com.nailcase.common.Image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "price_image")
@DiscriminatorValue("PRICE")
public class PriceImage extends Image {

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_info_id", nullable = false)
	private ShopInfo shopInfo;
}
