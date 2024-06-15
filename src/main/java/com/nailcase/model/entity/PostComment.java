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
@Schema(description = "게시물 댓글에 대한 엔티티")
@SuperBuilder
@Table(name = "post_comments")
public class PostComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id", nullable = false)
	private Long commentId;

	@Schema(title = "댓글 내용")
	@Column(name = "body", nullable = false, columnDefinition = "TEXT")
	private String body;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Post post;

	public void updateBody(String body) {
		this.body = body;
	}
}
