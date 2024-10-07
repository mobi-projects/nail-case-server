package com.nailcase.model.dto;

import java.time.LocalDateTime;

import com.nailcase.model.enums.NotificationType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NotificationDto {

	@Data
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Request {
		private Long senderId;
		private Long receiverId;
		private String content;
		private NotificationType notificationType;
		private LocalDateTime sendDateTime;
	}

	@Data
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long notificationId;
		private String content;
		private NotificationType notificationType;
		private Long senderId;
		private Long receiverId;
		private boolean isRead;
		private LocalDateTime sendDateTime;

		@Getter
		@Builder
		public static class GetListResponse {
			private Long id;
			private String content;
			private NotificationType notificationType;
			private LocalDateTime createdAt;
		}
	}
}