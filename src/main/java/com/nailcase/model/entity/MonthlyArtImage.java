package com.nailcase.model.entity;

import com.nailcase.common.Image;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Entity
@Table(name = "monthly_art_image")
@Getter
@NoArgsConstructor
@SuperBuilder
public class MonthlyArtImage extends Image {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "monthly_art_id")
	private MonthlyArt monthlyArt;

}