package com.nailcase.model.entity;

import java.util.HashSet;
import java.util.Set;

import com.nailcase.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
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

	@Builder.Default
	@OneToMany(mappedBy = "shopInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Size(min = 1, max = 5)
	private Set<PriceImage> priceImages = new HashSet<>(); // 초기화

	public void addPriceImage(PriceImage priceImage) {
		if (this.priceImages.size() < 5) {
			this.priceImages.add(priceImage);
			priceImage.setShopInfo(this);
		} else {
			throw new IllegalStateException("최대 5개의 가격 이미지만 등록할 수 있습니다.");
		}
	}

	public void clearPriceImages() {
		this.priceImages.clear();
	}

}
