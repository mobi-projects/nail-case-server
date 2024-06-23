package com.nailcase.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ImageService<T extends Image> {

	private final AmazonS3 amazonS3;
	private final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png");

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public void uploadImage(MultipartFile file, String objectName) {
		validateFile(file);

		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.getSize());
			objectMetadata.setContentType(file.getContentType());

			// 원본 파일명을 UTF-8로 인코딩하여 메타데이터에 저장
			String originalFilename = file.getOriginalFilename();
			assert originalFilename != null;
			String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
			objectMetadata.addUserMetadata("original-filename", encodedFilename);

			// UUID를 사용하여 고유한 objectName 생성
			String uniqueFileName = UUID.randomUUID() + "_" + encodedFilename;

			amazonS3.putObject(bucket, uniqueFileName, file.getInputStream(), objectMetadata);
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

	private void validateFile(MultipartFile file) {
		if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
			throw new BusinessException(ImageErrorCode.INVALID_FILE_TYPE);
		}

		// 5MB
		long MAX_FILE_SIZE = 10 * 1024 * 1024;
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new BusinessException(ImageErrorCode.FILE_TOO_LARGE);
		}
	}

	@Async("imageExecutor")
	public CompletableFuture<Void> deleteImageAsync(String objectName) {
		return CompletableFuture.runAsync(() -> {
			try {
				deleteImage(objectName);
			} catch (Exception e) {
				log.error("비동기 이미지 삭제 실패: {}", e.getMessage());
				throw e;
			}
		});
	}

	@Async("imageExecutor")
	public <T extends Image> CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, T image,
		JpaRepository<T, Long> imageRepository) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return saveImage(file, image, imageRepository);
			} catch (Exception e) {
				log.error("비동기 이미지 저장 실패: {}", e.getMessage());
				throw e;
			}
		});
	}

	@Async("imageExecutor")
	public <T extends Image> CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files,
		List<T> images, JpaRepository<T, Long> imageRepository) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return saveImages(files, images, imageRepository);
			} catch (Exception e) {
				log.error("비동기 이미지들 저장 실패: {}", e.getMessage());
				throw e;
			}
		});
	}
}
