package com.nailcase.model.entity;

import com.nailcase.common.Image;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "review_image")
@Getter
@DiscriminatorValue("REVIEW")
@NoArgsConstructor
@SuperBuilder
public class ReviewImage extends Image {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	public void updateReview(Review review) {
		this.review = review;
	}
}
