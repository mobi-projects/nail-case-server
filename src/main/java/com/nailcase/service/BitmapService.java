package com.nailcase.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.RedisErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BitmapService {

	private final RedisTemplate<String, Object> bitmapRedisTemplate;

	@Autowired
	public BitmapService(@Qualifier("bitmapRedisTemplate") RedisTemplate<String, Object> bitmapRedisTemplate) {
		this.bitmapRedisTemplate = bitmapRedisTemplate;
	}

	public void setBit(String key, long offset, boolean value) {
		try {
			bitmapRedisTemplate.opsForValue().setBit(key, offset, value);
		} catch (Exception e) {
			throw new BusinessException(RedisErrorCode.REDIS_OPERATION_FAILED, e);
		}
	}

	public Optional<Boolean> getBit(String key, long offset) {
		try {
			Boolean bit = bitmapRedisTemplate.opsForValue().getBit(key, offset);
			return Optional.ofNullable(bit);
		} catch (Exception e) {
			throw new BusinessException(RedisErrorCode.REDIS_OPERATION_FAILED, e);
		}
	}

	public Optional<Long> bitCount(String key) {
		return bitCount(key, 0, -1);
	}

	public Optional<Long> bitCount(String key, long start, long end) {
		try {
			Long count = bitmapRedisTemplate.execute((RedisCallback<Long>)connection -> {
				if (key != null) {
					return connection.stringCommands().bitCount(key.getBytes(), start, end);
				}
				return null;
			});
			return Optional.ofNullable(count);
		} catch (Exception e) {
			throw new BusinessException(RedisErrorCode.REDIS_OPERATION_FAILED, e);
		}
	}
}
