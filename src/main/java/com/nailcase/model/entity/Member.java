package com.nailcase.model.entity;

import java.util.List;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.Role;
import com.nailcase.model.enums.SocialType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "mambers")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long memberId;

	@Column(name = "name", length = 128)
	private String name;

	@Column(name = "email", length = 128)
	private String email;

/*	@Column(name = "phone", length = 128)
	private String phone;

	@Column(name = "password")
	private String password;*/

	@Enumerated(EnumType.STRING)
	@Column(name = "socialType")
	private SocialType socialType; // KAKAO, NAVER, FACEBOOK*/

	@Column(name = "socialId", unique = true)
	private String socialId;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@Column(name = "profileImgUrl", length = 128)
	private String profileImgUrl;

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Shop> shopList;

	// TODO
	public Member update(String name) {
		// this.name = name;
		return this;
	}

	public String getRoleKey() {
		return this.role.getKey();
	}
}
