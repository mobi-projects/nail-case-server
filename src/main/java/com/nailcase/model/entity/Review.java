package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.nailcase.common.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시물에 대한 엔티티")
@Table(name = "review")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id", nullable = false)
	private Long reviewId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_detail_id", nullable = false)
	private ReservationDetail reservationDetail;

	// @ManyToOne
	// @JoinColumn(name = "shop_member_id")
	// private ShopMember shopMember;

	@Schema(title = "리뷰 내용")
	@Column(name = "contents", nullable = false, columnDefinition = "TEXT")
	private String contents;

	@Schema(title = "리뷰 평점")
	@Column(name = "rating", nullable = false)
	private Double rating;

	@OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy("reviewCommentId asc")
	@Builder.Default
	private List<ReviewComment> reviewComments = new ArrayList<>();

	@OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ReviewImage> reviewImages = new ArrayList<>();

	@Column(name = "visit_count")
	private Integer visitCount;

	public void updateContents(String contents) {
		this.contents = contents;
	}

	public void updateRating(Double rating) {
		this.rating = rating;
	}

	public void registerReviewImages(List<ReviewImage> newImages) {
		if (reviewImages == null) {
			reviewImages = new ArrayList<>();
		}
		for (ReviewImage newImage : newImages) {
			addReviewImage(newImage);
		}
	}

	public void addReviewImage(ReviewImage reviewImage) {
		reviewImages.add(reviewImage);
		reviewImage.setReview(this);
	}

	public void removeReviewImage(ReviewImage reviewImage) {
		reviewImages.remove(reviewImage);
		reviewImage.setReview(null);
	}

	public void setReservationDetail(ReservationDetail reservationDetail) {
		this.reservationDetail = reservationDetail;
		if (reservationDetail != null && reservationDetail.getReview() != this) {
			reservationDetail.setReview(this);
		}
	}

	public void updateVisitCount(Integer visitCount) {
		this.visitCount = visitCount;
	}
}
