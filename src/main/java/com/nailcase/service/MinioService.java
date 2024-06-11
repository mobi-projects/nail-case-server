package com.nailcase.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;

@Service
public class MinioService {

	private final MinioClient minioClient;

	@Autowired
	public MinioService(MinioClient minioClient) {
		this.minioClient = minioClient;
	}

	public String uploadFile(String bucketName, String objectName, MultipartFile file) {
		try {
			minioClient.putObject(
				PutObjectArgs.builder()
					.bucket(bucketName)
					.object(objectName)
					.stream(file.getInputStream(), file.getSize(), -1)
					.contentType(file.getContentType())
					.build()
			);
			return minioClient.getPresignedObjectUrl(
				GetPresignedObjectUrlArgs.builder()
					.method(Method.GET)
					.bucket(bucketName)
					.object(objectName)
					.build()
			);
		} catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
			throw new BusinessException(ImageErrorCode.IMAGE_UPLOAD_ERROR, e);
		}
	}

	public void deleteFile(String bucketName, String objectName) {
		try {
			minioClient.removeObject(
				RemoveObjectArgs.builder()
					.bucket(bucketName)
					.object(objectName)
					.build()
			);
		} catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
			throw new BusinessException(ImageErrorCode.IMAGE_DELETE_ERROR, e);
		}
	}
}
