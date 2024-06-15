package com.nailcase.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// TODO User table, spring security 함께 작업 필요
@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
	@CreatedDate
	@Schema(title = "생성시간")
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Schema(title = "수정시간")
	@Column(name = "modified_at")
	private LocalDateTime modifiedAt;

	// @CreatedBy
	@Setter
	@Schema(title = "생성자")
	@Column(name = "create_by", nullable = false)
	private Long createdBy;

	// @LastModifiedBy
	@Setter
	@Schema(title = "수정자")
	@Column(name = "modified_by", nullable = false)
	private Long modifiedBy;

}