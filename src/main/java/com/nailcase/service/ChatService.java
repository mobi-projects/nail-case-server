package com.nailcase.service;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.AuthErrorCode;
import com.nailcase.model.dto.*;
import com.nailcase.model.entity.ChatMessage;
import com.nailcase.model.entity.ChatRoom;
import com.nailcase.model.entity.Member;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.enums.ChatRoomStatus;
import com.nailcase.model.enums.NotificationType;
import com.nailcase.model.enums.Role;
import com.nailcase.repository.ChatMessageRepository;
import com.nailcase.repository.ChatRoomRepository;
import com.nailcase.repository.MemberRepository;
import com.nailcase.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotificationService notificationService;


    public void createChatMessage(ChatRequestDto request) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdWithDetails(request.getChatRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new EntityNotFoundException("Shop not found"));

        // 메시지 생성시 발신자에 따라 해당 발신자의 읽음 상태를 true로 설정
        ChatMessage chatMessage = request.toEntity(chatRoom, member, shop);
        if (request.isShopMessage()) {
            chatMessage.markAsReadByShop();  // shop이 보냈으면 shop은 읽은 상태
        } else {
            chatMessage.markAsReadByMember();  // member가 보냈으면 member는 읽은 상태
        }

        chatRoom.addMessage(chatMessage);

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        ChatMessageDto messageDto = ChatMessageDto.of(savedMessage);

        // 발신자에 따라 다른 destination으로 메시지 전송
        messagingTemplate.convertAndSend("/exchange/chat.exchange/chat." + chatRoom.getChatRoomId(), messageDto);

        if (!request.isShopMessage()) {
            NotificationDto.Request notificationRequest = NotificationDto.Request.builder()
                    .senderId(member.getMemberId())
                    .receiverId(shop.getNailArtist().getNailArtistId())
                    .nickname(member.getNickname())
                    .content(chatMessage.getMessage())
                    .notificationType(NotificationType.NEW_CHAT_MESSAGE_TO_SHOP)
                    .reservationId(null)
                    .build();

            notificationService.sendNotificationToClient(notificationRequest);
        } else {  // shop이 보낸 메시지일 경우 고객에게 알림
            NotificationDto.Request notificationRequest = NotificationDto.Request.builder()
                    .senderId(shop.getNailArtist().getNailArtistId())
                    .receiverId(member.getMemberId())
                    .nickname(shop.getShopName())
                    .content(chatMessage.getMessage())
                    .notificationType(NotificationType.NEW_CHAT_MESSAGE_TO_MEMBER)
                    .reservationId(null)
                    .build();

            notificationService.sendNotificationToClient(notificationRequest);
        }
    }


    public ChatRoomDto createChatRoom(Long shopId, UserPrincipal userPrincipal) {

        if (userPrincipal.role() != Role.MEMBER) {
            throw new BusinessException(AuthErrorCode.INVALID_USER_TYPE);
        }
        Long memberId = userPrincipal.id();
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("Shop not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByShopAndMember(shop, member);

        if (existingRoom.isPresent()) {
            return ChatRoomDto.of(existingRoom.get());
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .shop(shop)
                .member(member)
                .name(shop.getShopName() + "-" + member.getNickname())
                .chatRoomStatus(ChatRoomStatus.ACTIVE)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomDto.of(savedRoom);
    }


    @Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 변경
    public ChatMessageDto.PageableResponse getChatMessages(Long chatRoomId, Pageable pageable) {
        Page<ChatMessage> messagePage = chatMessageRepository
                .findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable);

        List<ChatMessageDto> chatMessageDtos = messagePage.getContent()
                .stream()
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


    @Transactional(readOnly = true)
    public List<ChatRoomDto> getMemberChatRooms(Long memberId) {
        return chatRoomRepository.findByMemberIdAndChatRoomStatus(memberId, ChatRoomStatus.ACTIVE)
                .stream()
                .map(room -> ChatRoomDto.of(room, false))  // member 관점 명시
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDto> getShopChatRooms(Long shopId) {
        return chatRoomRepository.findByShopIdAndChatRoomStatus(shopId, ChatRoomStatus.ACTIVE)
                .stream()
                .map(room -> ChatRoomDto.of(room, true))  // shop 관점 명시
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDto> getShopUnreadChatRooms(Long shopId) {
        return chatRoomRepository.findByShopIdAndChatRoomStatus(shopId, ChatRoomStatus.ACTIVE)
                .stream()
                .filter(chatRoom -> chatRoom.getUnreadMessageCountForShop() > 0)
                .map(room -> ChatRoomDto.of(room, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsReadByMember(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdWithDetails(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        chatRoom.markMessagesAsReadByMember(memberId);
    }

    @Transactional
    public void markMessagesAsReadByShop(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomIdWithDetails(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found"));
        chatRoom.markMessagesAsReadByShop();
    }


}