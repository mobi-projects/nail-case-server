package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.ConditionOption;

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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "conditions")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Condition extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "condition_id")
	private Long conditionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_detail_id")
	private ReservationDetail reservationDetail;

	@Enumerated(EnumType.STRING)
	@Column(name = "option", nullable = false, length = 16)
	private ConditionOption option;

	public void associateDown(ReservationDetail reservationDetail) {
		this.reservationDetail = reservationDetail;
	}
}
