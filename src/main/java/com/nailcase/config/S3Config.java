package com.nailcase.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

	@Bean
	public AmazonS3 amazonS3(
		@Value("${cloud.aws.credentials.accessKey}") String accessKey,
		@Value("${cloud.aws.credentials.secretKey}") String secretKey,
		@Value("${cloud.aws.region.static}") String region,
		@Value("${cloud.aws.s3.endpoint}") String endpoint,
		@Value("${cloud.aws.s3.bucket}") String bucketName
	) {
		AmazonS3 amazonS3 = AmazonS3ClientBuilder
			.standard()
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
			.withPathStyleAccessEnabled(true)
			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
			.build();
		if (!amazonS3.doesBucketExistV2(bucketName)) {
			amazonS3.createBucket(bucketName);
			amazonS3.putObject(bucketName, "hello_world.txt", "hello world");
		}
		return amazonS3;
	}
}