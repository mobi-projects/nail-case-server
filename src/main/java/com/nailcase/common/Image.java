package com.nailcase.common;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Image extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long imageId;

	@Column(name = "bucket_name")
	private String bucketName;

	@Column(name = "object_name")
	private String objectName;

	public void setCreatedBy(Long createdBy) {
		super.setCreatedBy(createdBy);
	}

	public void setModifiedBy(Long modifiedBy) {
		super.setModifiedBy(modifiedBy);
	}
}
