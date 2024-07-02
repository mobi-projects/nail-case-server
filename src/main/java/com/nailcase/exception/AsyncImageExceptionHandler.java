package com.nailcase.exception;

import java.lang.reflect.Method;

import org.jetbrains.annotations.NotNull;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncImageExceptionHandler implements AsyncUncaughtExceptionHandler {

	@Override
	public void handleUncaughtException(@NotNull Throwable ex, Method method, Object... params) {
		log.error("비동기 이미지 처리 에러 발생");
		log.error("Exception message : {}", ex.getMessage());
		log.error("Method name : {}", method.getName());
		for (Object param : params) {
			log.error("Parameter value : {}", param);
		}
	}

}
