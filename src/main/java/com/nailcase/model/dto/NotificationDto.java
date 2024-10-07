package com.nailcase.model.dto;

import java.time.LocalDateTime;

import com.nailcase.model.enums.NotificationType;
import com.nailcase.model.enums.Role;

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
		private Role senderType;
		private Role receiverType;
		private LocalDateTime sendDateTime;
	}

	@Data
	@NoArgsConstructor(access = AccessLevel.PUBLIC)
	public static class Response {
		private Long notificationId;
		private String content;
		private NotificationType notificationType;
		private SenderInfo sender;
		private ReceiverInfo receiver;
		private LocalDateTime sendDateTime;

		@Data
		@NoArgsConstructor(access = AccessLevel.PUBLIC)
		public static class SenderInfo {
			private Long id;
			private String nickname;
			private String type; // "MEMBER" or "NAIL_ARTIST"
		}

		@Data
		@NoArgsConstructor(access = AccessLevel.PUBLIC)
		public static class ReceiverInfo {
			private Long id;
			private String nickname;
			private String type; // "MEMBER" or "NAIL_ARTIST"
		}

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