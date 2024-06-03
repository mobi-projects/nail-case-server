package com.nailcase.customer.domain;

import com.nailcase.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Schema(description = "사용자 상세 정보를 위한 도메인 객체")
@Entity
@Table(name = "Customers")
public class Customer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Schema(title = "사용자 이름")
	@Column(nullable = false, length = 32)
	private String name;

	@Schema(title = "사용자 이메일")
	@Column(nullable = false, length = 128)
	private String email;

	@Schema(title = "사용자 휴대폰번호")
	@Column(nullable = false, length = 32)
	private String phone;

	@Schema(title = "사용자 생성자")
	@Column(nullable = false, updatable = false)
	private Long createdBy;

	@Schema(title = "사용자 수정자")
	@Column(nullable = false)
	private Long modifiedBy;

	public void updatePhone(String phone) {
		this.phone = phone;
	}

	public void updateModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
