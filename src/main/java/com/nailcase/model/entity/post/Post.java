package com.nailcase.domain.post;

import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.nailcase.common.BaseEntity;
import com.nailcase.domain.post.comment.PostComment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시물에 대한 엔티티")
@Table(name = "posts")
public class Post extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id", nullable = false)
	private Long postId;

	@Schema(title = "게시물 제목 이름")
	@Column(name = "title", nullable = false, length = 32)
	private String title;

	@Schema(title = "게시물 내용")
	@Column(name = "contents", nullable = false)
	private String contents;

	@Schema(title = "좋아요 수")
	@Column(name = "likes", nullable = false)
	@ColumnDefault("0")
	private Long likes;

	@Schema(title = "좋아요 수")
	@Column(name = "likes", nullable = false)
	@ColumnDefault("0")
	private Long likes;

	@Schema(title = "즐겨찾기")
	@Column(name = "liked", nullable = false)
	@ColumnDefault("false")
	private Boolean liked;

	@OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) // 댓글은 한번에 다 불러와야 하므로 eager
	@OrderBy("commentId asc") // 댓글 정렬
	private List<PostComment> postComments;
}

