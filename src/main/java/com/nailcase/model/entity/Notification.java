package com.nailcase.model.entity;

import com.nailcase.common.BaseEntity;
import com.nailcase.model.enums.NotificationType;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_member_id")
	private Member senderMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_nail_artist_id")
	private NailArtist senderNailArtist;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_member_id")
	private Member receiverMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_nail_artist_id")
	private NailArtist receiverNailArtist;

	@Enumerated(EnumType.STRING)
	@Column(name = "notification_type")
	private NotificationType notificationType;

	// 발신자 설정 메서드
	public void updateSender(Object sender) {
		if (sender instanceof Member) {
			this.senderMember = (Member)sender;
		} else if (sender instanceof NailArtist) {
			this.senderNailArtist = (NailArtist)sender;
		} else {
			throw new IllegalArgumentException("Invalid sender type");
		}
	}

	// 수신자 설정 메서드
	public void updateReceiver(Object receiver) {
		if (receiver instanceof Member) {
			this.receiverMember = (Member)receiver;
		} else if (receiver instanceof NailArtist) {
			this.receiverNailArtist = (NailArtist)receiver;
		} else {
			throw new IllegalArgumentException("Invalid receiver type");
		}
	}

	// 발신자 조회 메서드
	public Object getSender() {
		return senderMember != null ? senderMember : senderNailArtist;
	}

	// 수신자 조회 메서드
	public Object getReceiver() {
		return receiverMember != null ? receiverMember : receiverNailArtist;
	}
}
