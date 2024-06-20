package com.nailcase.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "게시물에 대한 엔티티")
@Table(name = "posts")
@DynamicInsert
public class Post extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id", nullable = false)
	private Long postId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_id")
	private Shop shop;

	@Schema(title = "게시물 제목 이름")
	@Column(name = "title", nullable = false, length = 32)
	private String title;

	@Schema(title = "게시물 카테고리")
	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Schema(title = "게시물 내용")
	@Column(name = "contents", nullable = false, columnDefinition = "TEXT")
	private String contents;

	@Builder.Default
	@Schema(title = "좋아요 수")
	@Column(name = "likes", nullable = false)
	@ColumnDefault("0")
	private Long likes = 0L;

	@Builder.Default
	@Schema(title = "조회수")
	@Column(name = "views", nullable = false)
	@ColumnDefault("0")
	private Long views = 0L;

	@OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@OrderBy("commentId asc")
	private List<PostComment> postComments = new ArrayList<>();

	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostImage> postImages = new ArrayList<>();

	@Version
	private Long version;  // 낙관적 잠금을 위한 버전 필드

	public void incrementViews(Long viewCount) {
		this.views = viewCount;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void registerPostImages(List<PostImage> newImages) {
		if (postImages == null) {
			postImages = new ArrayList<>();
		}
		for (PostImage newImage : newImages) {
			addPostImage(newImage);
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

	public void addPostImage(PostImage postImage) {
		postImages.add(postImage);
		postImage.setPost(this);
	}

	public void removePostImage(PostImage postImage) {
		postImages.remove(postImage);
		postImage.setPost(null);
	}
}