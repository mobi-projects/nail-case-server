package com.nailcase.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Notification;
import com.nailcase.model.enums.Role;

public interface NotificationQuerydslRepository {

	Page<Notification> findByMemberReceiverId(Long memberId, Pageable pageable);

	Page<Notification> findByNailArtistReceiverId(Long nailArtistId, Pageable pageable);

	List<Notification> findByTypeAndReceiverId(Long receiverId, Role role);
}
