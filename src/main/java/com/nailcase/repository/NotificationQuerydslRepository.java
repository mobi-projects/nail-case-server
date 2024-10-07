package com.nailcase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nailcase.model.entity.Notification;

public interface NotificationQuerydslRepository {

	Page<Notification> findByMemberReceiverId(Long memberId, Pageable pageable);

	Page<Notification> findByNailArtistReceiverId(Long nailArtistId, Pageable pageable);

}
