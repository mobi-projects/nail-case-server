package com.nailcase.customer.domain;

import com.nailcase.common.BaseEntity;
import com.nailcase.customer.domain.dto.UpdateCustomerRequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "사용자 상세 정보를 위한 도메인 객체")
@Table(name = "customers")
public class Customer extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id", nullable = false)
	private Long customerId;

	@Schema(title = "사용자 이름")
	@Column(name = "name", nullable = false, length = 32)
	private String name;

	@Schema(title = "사용자 이메일")
	@Column(name = "email", nullable = false, length = 128)
	private String email;

	@Schema(title = "사용자 휴대폰번호")
	@Column(name = "phone", nullable = false, length = 32)
	private String phone;

	@Schema(title = "비밀번호")
	@Column(nullable = false)
	private String password;

	@Schema(title = "소셜 타입")
	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private SocialType socialType; // KAKAO, NAVER, FACEBOOK

	@Schema(title = "소셜 ID")
	@Column(nullable = true)
	private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	public void update(UpdateCustomerRequestDto c) {
		if (c.getName() != null) {
			this.name = c.getName();
		}
		if (c.getEmail() != null) {
			this.email = c.getEmail();
		}
		if (c.getPhone() != null) {
			this.phone = c.getPhone();
		}
	}

}