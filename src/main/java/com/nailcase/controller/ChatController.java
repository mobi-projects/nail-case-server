package com.nailcase.controller;

import com.nailcase.model.dto.ChatMessageDto;
import com.nailcase.model.dto.ChatRequestDto;
import com.nailcase.model.dto.ChatRoomDto;
import com.nailcase.model.dto.UserPrincipal;
import com.nailcase.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void handleMessage(ChatRequestDto chatRequest) {
        log.info("Received message: {}", chatRequest);
        chatService.createChatMessage(chatRequest);
    }

    @GetMapping("/chat/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDto>> getMemberChatRooms(@AuthenticationPrincipal Long memberId) {
        List<ChatRoomDto> chatRooms = chatService.getMemberChatRooms(memberId);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/chat/rooms")
    @ResponseBody
    public ResponseEntity<ChatRoomDto> createChatRoom(
            @RequestParam Long shopId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ChatRoomDto chatRoom = chatService.createChatRoom(shopId, userPrincipal);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/chat/rooms/{roomId}/messages")
    @ResponseBody
    public ResponseEntity<ChatMessageDto.PageableResponse> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ChatMessageDto.PageableResponse messages = chatService.getChatMessages(roomId, pageable);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/chat/rooms/{roomId}/messages/read")
    @ResponseBody
    public ResponseEntity<Void> markMessagesAsReadByMember(
            @PathVariable Long roomId,
            @RequestParam Long memberId) {
        chatService.markMessagesAsReadByMember(roomId, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chat/rooms/shop/{shopId}")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDto>> getShopChatRooms(@PathVariable Long shopId) {
        List<ChatRoomDto> chatRooms = chatService.getShopChatRooms(shopId);
        return ResponseEntity.ok(chatRooms);
    }

    // 선택적: 읽지 않은 메시지가 있는 채팅방만 조회
    @GetMapping("/chat/rooms/shop/{shopId}/unread")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDto>> getShopUnreadChatRooms(@PathVariable Long shopId) {
        List<ChatRoomDto> chatRooms = chatService.getShopUnreadChatRooms(shopId);
        return ResponseEntity.ok(chatRooms);
    }

    @PutMapping("/chat/rooms/{roomId}/messages/read/shop")
    @ResponseBody
    public ResponseEntity<Void> markMessagesAsReadByShop(
            @PathVariable Long roomId) {
        chatService.markMessagesAsReadByShop(roomId);
        return ResponseEntity.ok().build();
    }
}