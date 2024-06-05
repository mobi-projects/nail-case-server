package com.nailcase.customer.domain;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.nailcase.common.BaseEntity;

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
	@Column(nullable = false, length = 128, unique = true)
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

	public void updatePhone(String phone) {
		this.phone = phone;
	}

	public void updateModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void authorizeUser() {
		this.role = Role.GUEST;
	}

	public void passwordEncode(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(this.password);
	}

}
