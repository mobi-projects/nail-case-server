package com.nailcase.service;

import org.springframework.stereotype.Service;

import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.model.entity.ChatMessage;
import com.nailcase.repository.ChattingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChattingService {

	private final ChattingRepository chattingRepository;

	public ChatMessage saveMessage(ChatMessageDto messageDto) {
		// 이 부분은 ChatRoom entity와 연결되어 있어야 함

		return chattingRepository.save(messageDto.toEntity(messageDto));
	}
}
