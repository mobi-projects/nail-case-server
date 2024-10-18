package com.nailcase.service;

import static com.nailcase.exception.codes.ShopErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailcase.exception.BusinessException;
import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.ChatRoom;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.ChatRoomRepository;
import com.nailcase.repository.ChattingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ChatRoomService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChattingRepository chatMessageRepository;
	private final RabbitTemplate rabbitTemplate;

	private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
	private final static String CHAT_ROUTING_KEY = "room.";

	@Transactional
	public void saveAndSendMessage(Long shopId, ChatMessageDto message, String chatRoomId) {
		try {
			checkByShopIdAndRoomId(shopId, message.getChatRoomId());

			// 1. 메시지 저장
			ChatMessage savedMessage = chatMessageRepository.save(message.toEntity(message));

			// 2. RabbitMQ로 메시지 전송
			String routingKey = String.format("shop.%d.room.%s", shopId, chatRoomId);
			rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, routingKey, ChatMessageDto.of(savedMessage));

			log.info("Message saved and sent successfully. ChatRoomId: {}, MessageId: {}",
				chatRoomId, savedMessage.getChatMessageId());
		} catch (BusinessException e) {
			log.error("Error in saveAndSendMessage. ShopId: {}, ChatRoomId: {}", shopId, chatRoomId, e);
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error in saveAndSendMessage. ShopId: {}, ChatRoomId: {}",
				shopId, chatRoomId, e);
			throw new BusinessException(CHAT_PROCESS_FAILED);
		}
	}

	private void checkByShopIdAndRoomId(Long shopId, Long roomId) {
		if (!chatRoomRepository.existsByShopIdAndChatRoomId(shopId, roomId)) {
			throw new BusinessException(CHAT_ROOM_NOT_FOUND);
		}
	}

	public ChatMessageDto.PageableResponse enterShopChatRoom(UserPrincipal userPrincipal, Long shopId,
		int page, int size) {
		ChatRoom andCheckBeforeEnterRoom = findAndCheckBeforeEnterRoom(shopId, userPrincipal.id(),
			userPrincipal.role());

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomIdWithPagination(
			andCheckBeforeEnterRoom.getChatRoomId(), pageable);

		List<ChatMessageDto> chatMessageDtos = messagePage.getContent().stream()
			.map(ChatMessageDto::of)
			.collect(Collectors.toList());

		return new ChatMessageDto.PageableResponse(
			chatMessageDtos,
			messagePage.getNumber(),
			messagePage.getSize(),
			messagePage.getTotalElements(),
			messagePage.getTotalPages(),
			messagePage.isLast()
		);
	}

	@Transactional
	public ChatRoom findAndCheckBeforeEnterRoom(Long shopId, Long memberId, Role role) {
		return chatRoomRepository.findChatRoomByShopIdAndMemberId(shopId, memberId)
			.orElseGet(() -> {
				if (Role.MEMBER.equals(role)) {
					return createAndSaveChatRoom(shopId, memberId);
				} else {
					throw new BusinessException(CHAT_ROOM_NOT_FOUND);
				}
			});
	}

	private ChatRoom createAndSaveChatRoom(Long shopId, Long memberId) {
		ChatRoom newChatRoom = ChatRoom.builder()
			.shop(Shop.builder().shopId(shopId).build())
			.member(Member.builder().memberId(memberId).build())
			.build();
		return chatRoomRepository.save(newChatRoom);
	}

	public ChatMessageDto.PageableResponse managerEnterRoom(UserPrincipal userPrincipal, Long shopId, String chatRoomId,
		int page, int size) {

		ChatRoom chatRoom = chatRoomRepository.findChatRoomByShopIdAndChatRoomId(shopId, Long.valueOf(chatRoomId))
			.orElseThrow(() -> new BusinessException(CHAT_ROOM_NOT_FOUND));

		// 매니저가 아닌 경우
		if (!chatRoom.getShop().hasNailArtist(userPrincipal.id())) {
			throw new BusinessException(CHAT_ROOM_FORBIDDEN);
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<ChatMessage> messagePage = chatMessageRepository.findByChatRoomIdWithPagination(
			chatRoom.getChatRoomId(), pageable);

		List<ChatMessageDto> chatMessageDtos = messagePage.getContent().stream()
			.map(ChatMessageDto::of)
			.collect(Collectors.toList());

		return new ChatMessageDto.PageableResponse(
			chatMessageDtos,
			messagePage.getNumber(),
			messagePage.getSize(),
			messagePage.getTotalElements(),
			messagePage.getTotalPages(),
			messagePage.isLast()
		);
	}

}