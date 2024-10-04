package com.nailcase.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Page<Notification> findByReceiverMember_MemberId(Long memberId, Pageable pageable);

	Page<Notification> findByReceiverNailArtist_NailArtistId(Long nailArtistId, Pageable pageable);
}

