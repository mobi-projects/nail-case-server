package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.TreatmentOption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "treatments")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Treatment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "treatment_id")
	private Long treatmentId;

	@Enumerated(EnumType.STRING)
	@Column(name = "option", nullable = false, length = 16)
	private TreatmentOption option;

	// 이달의 아트
	@Column(name = "image_id")
	private Long imageId;

	@Column(name = "image_url", length = 1000)
	private String imageUrl;

	public void updateImageId(Long imageId) {
		this.imageId = imageId;
	}

	public void updateImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
