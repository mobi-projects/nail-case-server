package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "monthly_art")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyArt extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "monthly_art_id")
	private Long monthlyArtId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@Schema(title = "이달의 아트 제목")
	@Column(name = "title", nullable = false, length = 32)
	private String title;

	@Schema(title = "이달의 아트 내용")
	@Column(name = "contents", nullable = false, columnDefinition = "TEXT")
	private String contents;

	@Builder.Default
	@Schema(title = "조회수")
	@Column(name = "views", nullable = false)
	@ColumnDefault("0")
	private Long views = 0L;

	@Builder.Default
	@Schema(title = "좋아요 수")
	@Column(name = "likes", nullable = false)
	@ColumnDefault("0")
	private Long likes = 0L;

	@Setter
	@OneToMany(mappedBy = "monthlyArt", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MonthlyArtImage> monthlyArtImages = new ArrayList<>();

	@OneToMany(mappedBy = "monthlyArt", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@OrderBy("commentId asc")
	private List<MonthlyArtComment> monthlyArtComments = new ArrayList<>();

	@Version
	private Long version;  // 낙관적 잠금을 위한 버전 필드

	public void incrementViews(Long viewCount) {
		this.views = viewCount;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void registerPostImages(List<MonthlyArtImage> newImages) {
		if (monthlyArtImages == null) {
			monthlyArtImages = new ArrayList<>();
		}
		for (MonthlyArtImage newImage : newImages) {
			addImage(newImage);
		}
	}

	public void updateContents(String contents) {
		this.contents = contents;
	}

	public void incrementLikes() {
		this.likes += 1;
	}

	public void decrementLikes() {
		this.likes -= 1;
	}

	public void addImage(MonthlyArtImage monthlyArtImage) {
		monthlyArtImages.add(monthlyArtImage);
		monthlyArtImage.setMonthlyArt(this);
	}

	public void removeImage(MonthlyArtImage monthlyArtImage) {
		monthlyArtImages.remove(monthlyArtImage);
		monthlyArtImage.setMonthlyArt(null);
	}
}
