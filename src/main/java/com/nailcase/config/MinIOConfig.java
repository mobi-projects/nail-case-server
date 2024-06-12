package com.nailcase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinIOConfig {
	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${cloud.aws.s3.endpoint}")
	private String endpoint;

	@Bean
	public MinioClient minioClient() {
		return MinioClient.builder()
			.credentials(accessKey, secretKey)
			.endpoint(endpoint)
			.build();
	}
}