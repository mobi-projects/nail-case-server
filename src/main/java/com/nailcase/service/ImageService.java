package com.nailcase.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService<T extends Image> {

	private final AmazonS3 amazonS3;
	private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png");
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Async("imageExecutor")
	public CompletableFuture<Void> deleteImageAsync(String objectName) {
		return CompletableFuture.runAsync(() -> {
			try {
				amazonS3.deleteObject(bucket, objectName);
			} catch (Exception e) {
				log.error("비동기 이미지 삭제 실패", e);
				throw new BusinessException(ImageErrorCode.DELETE_FAILURE, e);
			}
		});
	}

	@Async("imageExecutor")
	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, T image,
		JpaRepository<T, Long> imageRepository) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				String objectName = generateUniqueObjectName(file.getOriginalFilename());
				objectName = uploadImageAsync(file, objectName).join();
				image.setBucketName(bucket);
				image.setObjectName(objectName);
				T savedImage = imageRepository.save(image);
				return mapToImageDto(savedImage);
			} catch (Exception e) {
				log.error("이미지 저장 실패", e);
				throw new BusinessException(ImageErrorCode.SAVE_FAILURE, e);
			}
		});
	}

	@Async("imageExecutor")
	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<T> images,
		JpaRepository<T, Long> imageRepository) {
		List<CompletableFuture<ImageDto>> futures = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			futures.add(saveImageAsync(files.get(i), images.get(i), imageRepository));
		}
		CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		return allFutures.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
	}

	@Async("imageExecutor")
	public CompletableFuture<String> uploadImageAsync(MultipartFile file, String objectName) {
		return CompletableFuture.supplyAsync(() -> {
			validateFile(file);
			try {
				ObjectMetadata objectMetadata = new ObjectMetadata();
				objectMetadata.setContentLength(file.getSize());
				objectMetadata.setContentType(file.getContentType());
				String originalFilename = file.getOriginalFilename();
				assert originalFilename != null;
				String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
				objectMetadata.addUserMetadata("original-filename", encodedFilename);
				amazonS3.putObject(bucket, objectName, file.getInputStream(), objectMetadata);
				return objectName;
			} catch (Exception e) {
				log.error("이미지 업로드 실패", e);
				throw new BusinessException(ImageErrorCode.UPLOAD_FAILURE, e);
			}
		});
	}

	@Async("imageExecutor")
	public CompletableFuture<ResponseEntity<byte[]>> downloadImageAsync(String objectName) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				S3Object s3Object = amazonS3.getObject(bucket, objectName);
				S3ObjectInputStream inputStream = s3Object.getObjectContent();
				byte[] fileData = IOUtils.toByteArray(inputStream);
				String originalFilename = s3Object.getObjectMetadata().getUserMetaDataOf("original-filename");
				HttpHeaders headers = new HttpHeaders();
				headers.setContentDisposition(
					ContentDisposition.builder("attachment")
						.filename(originalFilename, StandardCharsets.UTF_8)
						.build());
				headers.setContentType(MediaType.IMAGE_JPEG);
				return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
			} catch (Exception e) {
				log.error("이미지 다운로드 실패", e);
				throw new BusinessException(ImageErrorCode.DOWNLOAD_FAILURE, e);
			}
		});
	}

	private String generateUniqueObjectName(String originalFilename) {
		return UUID.randomUUID() + "_" + originalFilename;
	}

	private String generateImageUrl(String objectName) {
		return amazonS3.getUrl(bucket, objectName).toString();
	}

	private void validateFile(MultipartFile file) {
		if (!ALLOWED_MIME_TYPES.contains(file.getContentType())) {
			log.warn("허용되지 않는 파일 타입: {}", file.getContentType());
			throw new BusinessException(ImageErrorCode.INVALID_FILE_TYPE);
		}
		if (file.getSize() > MAX_FILE_SIZE) {
			log.warn("파일 크기 초과: {} bytes", file.getSize());
			throw new BusinessException(ImageErrorCode.FILE_TOO_LARGE);
		}
	}

	private ImageDto mapToImageDto(T image) {
		return ImageDto.builder()
			.id(image.getImageId())
			.bucketName(image.getBucketName())
			.objectName(image.getObjectName())
			.url(generateImageUrl(image.getObjectName()))
			.build();
	}
}
