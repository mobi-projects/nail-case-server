package com.nailcase.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class EmitterRepository {
	private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

	public SseEmitter save(Long userId, SseEmitter sseEmitter) {
		emitterMap.put(getKey(userId), sseEmitter);
		log.info("Saved SseEmitter for {}", userId);
		return sseEmitter;
	}

	public Optional<SseEmitter> get(Long userId) {
		return Optional.ofNullable(emitterMap.get(getKey(userId)));
	}

	public void delete(Long userId) {
		emitterMap.remove(getKey(userId));
		log.info("Deleted SseEmitter for {}", userId);
	}

	private String getKey(Long userId) {
		return "Emitter:UID:" + userId;
	}
}
