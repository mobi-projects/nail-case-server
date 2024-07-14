package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "리뷰 댓글에 대한 엔티티")
@SuperBuilder
@Table(name = "review_comments")
public class ReviewComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_comment_id", nullable = false)
	private Long reviewCommentId;

	@Schema(title = "리뷰 댓글 내용")
	@Column(name = "contents", nullable = false, columnDefinition = "TEXT")
	private String contents;

	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review;

	public void updateContents(String contents) {
		this.contents = contents;
	}

}
