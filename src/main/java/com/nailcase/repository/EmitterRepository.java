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

	public SseEmitter save(String emitterKey, SseEmitter sseEmitter) {
		emitterMap.put(emitterKey, sseEmitter);
		log.info("Saved SseEmitter for key: {}", emitterKey);
		return sseEmitter;
	}

	public Optional<SseEmitter> get(String emitterKey) {
		return Optional.ofNullable(emitterMap.get(emitterKey));
	}

	public void delete(String emitterKey) {
		emitterMap.remove(emitterKey);
		log.info("Deleted SseEmitter for key: {}", emitterKey);
	}
}
