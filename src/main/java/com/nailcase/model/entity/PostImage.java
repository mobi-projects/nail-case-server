package com.nailcase.model.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.nailcase.common.Image;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@SuperBuilder
public class PostImage extends Image {

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post;

}