package com.nailcase.service;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ImageService {

	private final MinioClient minioClient;

	public void uploadImage(MultipartFile file, String bucketName, String objectName) {
		try {
			boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
			if (!bucketExists) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
			}
			minioClient.putObject(PutObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.stream(file.getInputStream(), file.getSize(), -1)
				.build());
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.UPLOAD_FAILURE, e);
		}
	}

	public InputStream downloadImage(String bucketName, String objectName) {
		try {
			return minioClient.getObject(GetObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.build());
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.DOWNLOAD_FAILURE, e);
		}
	}

	public void deleteImage(String bucketName, String objectName) {
		try {
			minioClient.removeObject(RemoveObjectArgs.builder()
				.bucket(bucketName)
				.object(objectName)
				.build());
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.DELETE_FAILURE, e);
		}
	}
}
