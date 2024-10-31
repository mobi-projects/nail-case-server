package com.nailcase.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    @GetMapping("/chat/inbox")
    public ResponseEntity<Void> handleWebSocketEndpoint(HttpServletRequest request, HttpServletResponse response) {
        log.info("WebSocket handshake request received from: {}", request.getRemoteAddr());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Upgrade", "websocket");
        headers.add("Connection", "Upgrade");
        headers.add("Sec-WebSocket-Accept", "true");

        return ResponseEntity
                .status(HttpStatus.SWITCHING_PROTOCOLS)
                .headers(headers)
                .build();
    }

    @MessageMapping("/chat/connect")
    public void handleConnect(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("New WebSocket connection established. Session ID: {}", sessionId);
    }

    @MessageMapping("/chat/disconnect")
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("WebSocket connection closed. Session ID: {}", sessionId);
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("User disconnected: {}", sessionId);
    }
}