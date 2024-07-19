package com.nailcase.model.entity;

import java.util.Objects;

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

@Entity
@Table(name = "post_image")
@Getter
@NoArgsConstructor
@SuperBuilder
public class PostImage extends Image {

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

	public PostImage(Post post) {
		this.post = post;
	}

	public PostImage(Post post, String bucketName, String objectName) {
		super(null, bucketName, objectName);  // imageId는 null로 설정 (자동 생성될 것이므로)
		this.post = post;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof PostImage postImage))
			return false;
		if (!super.equals(o))
			return false;
		return Objects.equals(getPost(), postImage.getPost());
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getPost());
	}
}