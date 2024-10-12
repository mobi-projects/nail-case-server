package com.nailcase.model.dto;

import com.nailcase.model.enums.NotificationType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NotificationDto {

	@Data
	@Builder
	public static class Request {
		private Long reservationId;
		private String nickname;
		private String content;
		private NotificationType notificationType;
		private Long senderId;
		private Long receiverId;
		private boolean isRead;
		private Long sendDateTime;
		private Long startTime;
		private Long endTime;
	}

	@Data
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long notificationId;
		private Long reservationId;
		private String nickname;
		private String content;
		private NotificationType notificationType;
		private Long senderId;
		private Long receiverId;
		private boolean isRead;
		private Long sendDateTime;
		private Long startTime;
		private Long endTime;

	}
}