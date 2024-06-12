// ImageService
package com.nailcase.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.nailcase.common.Image;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.repository.ImageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class ImageService {

	private final AmazonS3 amazonS3;
	private final ImageRepository imageRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public void uploadImage(MultipartFile file, String objectName) {
		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.getSize());
			objectMetadata.setContentType(file.getContentType());

			amazonS3.putObject(bucket, objectName, file.getInputStream(), objectMetadata);
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.UPLOAD_FAILURE, e);
		}
	}

	public byte[] downloadImage(String objectName) {
		try {
			S3Object s3Object = amazonS3.getObject(bucket, objectName);
			S3ObjectInputStream inputStream = s3Object.getObjectContent();
			return IOUtils.toByteArray(inputStream);
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.DOWNLOAD_FAILURE, e);
		}
	}

	public void deleteImage(String objectName) {
		try {
			amazonS3.deleteObject(bucket, objectName);
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.DELETE_FAILURE, e);
		}
	}

	@Transactional
	public ImageDto saveImage(MultipartFile file, Image image) {
		try {
			String objectName = generateUniqueObjectName(file.getOriginalFilename());

			uploadImage(file, objectName);

			image.setBucketName(bucket);
			image.setObjectName(objectName);

			Image savedImage = imageRepository.save(image);

			return ImageDto.builder()
				.id(savedImage.getId())
				.bucketName(savedImage.getBucketName())
				.objectName(savedImage.getObjectName())
				.url(generateImageUrl(savedImage.getObjectName()))
				.build();
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.SAVE_FAILURE, e);
		}
	}

	private String generateUniqueObjectName(String originalFilename) {
		return UUID.randomUUID() + "_" + originalFilename;
	}

	private String generateImageUrl(String objectName) {
		return amazonS3.getUrl(bucket, objectName).toString();
	}
}