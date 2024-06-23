package com.nailcase.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.nailcase.exception.AsyncImageExceptionHandler;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

	@Bean(name = "imageExecutor")
	public Executor imageExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setThreadGroupName("imageExecutor");
		executor.setThreadNamePrefix("Image-");
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(50);
		// 작업 실행 시간 제한 설정 (5분)
		executor.setKeepAliveSeconds(300);

		executor.initialize();

		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncImageExceptionHandler();
	}

}