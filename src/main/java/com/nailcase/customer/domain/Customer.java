package com.nailcase.customer.domain;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드를 초기화하는 생성자를 private로 생성
@Builder
@Schema(description = "사용자 상세 정보를 위한 도메인 객체")
@Entity
@Table(name = "Customers")
public class Customer {

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

	@Schema(title = "사용자 생성일")
	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Schema(title = "사용자 수정일")
	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime modifiedAt;

	@Schema(title = "사용자 생성자")
	@Column(nullable = false, updatable = false)
	private Long createdBy;

	@Schema(title = "사용자 수정자")
	@Column(nullable = false)
	private Long modifiedBy;


	//
	// @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
	// @JoinColumn(name = "appointment_id")
	// private Appointment appointment;
}
