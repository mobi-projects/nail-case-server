package com.nailcase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Notification;
import com.nailcase.model.enums.Role;

public interface NotificationQuerydslRepository {

	Page<Notification> findByMemberReceiverId(Long memberId, Pageable pageable);

	Page<Notification> findByNailArtistReceiverId(Long nailArtistId, Pageable pageable);

	Notification findByTypeAndReceiverIdWithNotSent(Long receiverId, Role role);
}
