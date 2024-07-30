package com.nailcase.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService<T extends Image> {

	private final AmazonS3 amazonS3;
	private final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public void uploadImage(MultipartFile file, String objectName) {
		validateFile(file);
		log.info("Uploading file: original filename = {}, encoded filename = {}", file.getOriginalFilename(),
			objectName);

		try {
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.getSize());
			objectMetadata.setContentType(file.getContentType());
			objectMetadata.addUserMetadata("original-filename",
				URLEncoder.encode(Objects.requireNonNull(file.getOriginalFilename()), StandardCharsets.UTF_8));

			byte[] bytes = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(bytes);

			amazonS3.putObject(bucket, objectName, inputStream, objectMetadata);
		} catch (Exception e) {
			log.error("Failed to upload file: original filename = {}, encoded filename = {}",
				file.getOriginalFilename(), objectName, e);
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

	@Transactional
	public List<ImageDto> saveImagesSync(List<MultipartFile> files, List<T> images,
		JpaRepository<T, Long> imageRepository) {
		List<ImageDto> savedImages = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			T image = images.get(i);
			try {
				String objectName = generateUniqueObjectName(Objects.requireNonNull(file.getOriginalFilename()));
				uploadImage(file, objectName);

				image.setBucketName(bucket);
				image.setObjectName(objectName);

				T savedImage = imageRepository.save(image);

				ImageDto imageDto = ImageDto.builder()
					.id(savedImage.getImageId())
					.bucketName(savedImage.getBucketName())
					.objectName(savedImage.getObjectName())
					.url(generateImageUrl(savedImage.getObjectName()))
					.createdBy(savedImage.getCreatedBy())
					.modifiedBy(savedImage.getModifiedBy())
					.build();

				savedImages.add(imageDto);
				log.info("Saved image: {}", imageDto);
			} catch (Exception e) {
				log.error("Failed to save image at index {}", i, e);
				throw e;  // 예외를 다시 던져서 트랜잭션이 롤백되도록 함
			}
		}
		return savedImages;
	}

	public <T extends Image> ImageDto saveImage(MultipartFile file, T image, JpaRepository<T, Long> imageRepository) {
		try {
			String objectName = generateUniqueObjectName(Objects.requireNonNull(file.getOriginalFilename()));
			log.debug("Generated object name: {}", objectName);

			uploadImage(file, objectName);

			image.setBucketName(bucket);
			image.setObjectName(objectName);

			T savedImage = imageRepository.save(image);
			log.debug("Saved image: {}", savedImage);

			return ImageDto.builder()
				.id(savedImage.getImageId())
				.bucketName(savedImage.getBucketName())
				.objectName(savedImage.getObjectName())
				.url(generateImageUrl(savedImage.getObjectName()))
				.createdBy(savedImage.getCreatedBy())
				.modifiedBy(savedImage.getModifiedBy())
				.build();
		} catch (Exception e) {
			log.error("Failed to save image", e);
			throw new BusinessException(ImageErrorCode.SAVE_FAILURE, e);
		}
	}

	private String generateUniqueObjectName(String originalFilename) {
		int lastDotIndex = originalFilename.lastIndexOf('.');
		String nameWithoutExtension = originalFilename.substring(0, lastDotIndex);
		String extension = originalFilename.substring(lastDotIndex);

		String encodedName = URLEncoder.encode(nameWithoutExtension, StandardCharsets.UTF_8);
		return UUID.randomUUID() + "_" + encodedName + extension;
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
}
