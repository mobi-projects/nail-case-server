package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.NotificationType;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@Table(name = "notifications")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long notificationId;

	@Column(name = "content", length = 2048)
	private String content;

	private Long senderId;

	private Long receiverId;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type")
	private NotificationType notificationType;

	// 읽었는지 여부 필드
	@Builder.Default
	@Column(name = "is_read")
	private boolean isRead = false;

	// 보냄처리 여부
	@Builder.Default
	@Column(name = "is_sent")
	private boolean isSent = false;

	public void markAsSent() {
		this.isSent = true;
	}

	public void read() {
		this.isRead = true;
	}

	public void updateSender(Long senderId) {
		this.senderId = senderId;
	}

	public void updateReceiver(Long receiverId) {
		this.receiverId = receiverId;
	}
}
