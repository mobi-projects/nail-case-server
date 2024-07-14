package com.nailcase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		// 들여쓰기를 사용하여 JSON을 보기 좋게 출력 (선택 사항)
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		// 날짜/시간을 ISO-8601 형식으로 직렬화 (선택 사항)
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper;
	}
}