package com.nailcase.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.nailcase.common.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Schema(description = "이달의 아트 좋아요 리스트 엔티티")
@Table(name = "monthly_art_liked_member")
public class MonthlyArtLikedMember extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "monthly_art_id")
	private MonthlyArt monthlyArt;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@CreatedDate
	private LocalDateTime likedAt;

	public void updateMonthlyArt(MonthlyArt monthlyArt) {
		this.monthlyArt = monthlyArt;
	}

	public void updateMember(Member member) {
		this.member = member;
	}

}
