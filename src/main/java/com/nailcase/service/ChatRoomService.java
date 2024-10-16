package com.nailcase.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.model.dto.ChatRoomDto;
import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.ChatRoom;
import com.nailcase.repository.ChatRoomRepository;
import com.nailcase.repository.ChattingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChattingRepository chatMessageRepository;

	public boolean existsByChatRoomId(Long roomId) {
		return chatRoomRepository.existsByChatRoomId(roomId);
	}

	public List<ChatRoomDto> findAll() {
		List<ChatRoom> all = chatRoomRepository.findAll();

		return all.stream()
			.map(ChatRoomDto::of)
			.collect(Collectors.toList());
	}

	@Transactional
	public String saveRoom(ChatRoomDto chatRoom) {
		ChatRoom save = chatRoomRepository.save(chatRoom.toEntity());
		return save.getName();
	}

	public ChatRoomDto findByRoomId(Long roomId) {
		ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(roomId)
			.orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
		return ChatRoomDto.of(chatRoom);
	}

	public List<ChatMessageDto> findMessagesByRoomId(Long roomId) {
		List<ChatMessage> messages = chatMessageRepository.findMessagesByChatRoomId(roomId);

		return messages.stream()
			.map(ChatMessageDto::of)
			.toList();
	}

}
