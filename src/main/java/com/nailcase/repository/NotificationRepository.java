package com.nailcase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nailcase.model.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationQuerydslRepository {

}

