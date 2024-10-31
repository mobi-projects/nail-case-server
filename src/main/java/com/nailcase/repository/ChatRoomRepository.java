package com.nailcase.repository;

import com.nailcase.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomQuerydslRepository {


}
