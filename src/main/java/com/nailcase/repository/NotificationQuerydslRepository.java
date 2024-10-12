package com.nailcase.repository;

import java.util.List;

import com.nailcase.model.entity.Notification;
import com.nailcase.model.enums.Role;

public interface NotificationQuerydslRepository {

	List<Notification> findByMemberReceiverId(Long memberId);

	List<Notification> findByNailArtistReceiverId(Long nailArtistId);

	List<Notification> findByTypeAndReceiverIdWithNotRead(Long receiverId, Role role);

	void updateReadStatusInNotReadNotification(Long receiverId, Role role);
}
