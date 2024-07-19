package com.nailcase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TransactionConfig {

	private final PlatformTransactionManager transactionManager;

	@Bean
	public TransactionTemplate transactionTemplate() {
		return new TransactionTemplate(transactionManager);
	}

}
