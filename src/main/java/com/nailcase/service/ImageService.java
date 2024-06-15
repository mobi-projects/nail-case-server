package com.nailcase.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class ImageService<T extends Image> {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public void uploadImage(MultipartFile file, String objectName) {
		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.getSize());
			objectMetadata.setContentType(file.getContentType());

			objectMetadata.addUserMetadata("original-filename", file.getOriginalFilename());

			amazonS3.putObject(bucket, objectName, file.getInputStream(), objectMetadata);
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.UPLOAD_FAILURE, e);
		}
	}

	public ResponseEntity<byte[]> downloadImage(String objectName) {
		try {
			S3Object s3Object = amazonS3.getObject(bucket, objectName);
			S3ObjectInputStream inputStream = s3Object.getObjectContent();
			byte[] fileData = IOUtils.toByteArray(inputStream);

			String originalFilename = s3Object.getObjectMetadata().getUserMetaDataOf("original-filename");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(ContentDisposition.builder("attachment")
				.filename(originalFilename, StandardCharsets.UTF_8)
				.build());

			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
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

	public <T extends Image> ImageDto saveImage(MultipartFile file, T image, JpaRepository<T, Long> imageRepository) {
		try {
			String objectName = generateUniqueObjectName(file.getOriginalFilename());

			uploadImage(file, objectName);

			image.setBucketName(bucket);
			image.setObjectName(objectName);

			T savedImage = imageRepository.save(image);
			return ImageDto.builder()
				.id(savedImage.getImageId())
				.bucketName(savedImage.getBucketName())
				.objectName(savedImage.getObjectName())
				.url(generateImageUrl(savedImage.getObjectName()))
				.createdBy(savedImage.getCreatedBy())
				.modifiedBy(savedImage.getModifiedBy())
				.build();
		} catch (Exception e) {
			throw new BusinessException(ImageErrorCode.SAVE_FAILURE, e);
		}
	}

	public <T extends Image> List<ImageDto> saveImages(List<MultipartFile> files, List<T> images,
		JpaRepository<T, Long> imageRepository) {
		List<ImageDto> savedImageDtos = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			T image = images.get(i);
			ImageDto savedImageDto = saveImage(file, image, imageRepository);
			savedImageDtos.add(savedImageDto);
		}
		return savedImageDtos;
	}

	private String generateUniqueObjectName(String originalFilename) {
		return UUID.randomUUID() + "_" + originalFilename;
	}

	private String generateImageUrl(String objectName) {
		return amazonS3.getUrl(bucket, objectName).toString();
	}
}
