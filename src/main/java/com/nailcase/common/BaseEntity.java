package com.nailcase.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Schema(title = "수정시간")
	@Column(name = "modified_at")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime modifiedAt;

	@CreatedBy
	@Setter
	@Schema(title = "생성자")
	@Column(name = "create_by")
	private Long createdBy;

	@LastModifiedBy
	@Setter
	@Schema(title = "수정자")
	@Column(name = "modified_by")
	private Long modifiedBy;
}