package com.nailcase.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.model.entity.PostImage;
import com.nailcase.repository.PostImageRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostImageService extends ImageService<PostImage> {
	private final PostImageRepository postImageRepository;

	@Autowired
	public PostImageService(
		AmazonS3 amazonS3,
		PostImageRepository postImageRepository,
		@Qualifier("imageExecutor") Executor imageExecutor) {
		super(imageExecutor, amazonS3);
		this.postImageRepository = postImageRepository;
	}

	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, PostImage image, Authentication auth) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.saveImageAsync(file, image, postImageRepository, auth).get();
			} catch (Exception e) {
				log.error("이미지 저장 실패", e);
				throw new BusinessException(ImageErrorCode.SAVE_FAILURE);
			}
		}, imageExecutor);
	}

	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<PostImage> images,
		Authentication auth) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.saveImagesAsync(files, images, postImageRepository, auth).get();
			} catch (Exception e) {
				log.error("이미지들 저장 실패", e);
				throw new BusinessException(ImageErrorCode.SAVE_FAILURE);
			}
		}, imageExecutor);
	}

	public CompletableFuture<Void> deleteImageAsync(String objectName) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				super.deleteImageAsync(objectName).get();
				return null;
			} catch (Exception e) {
				log.error("이미지 삭제 실패", e);
				throw new BusinessException(ImageErrorCode.DELETE_FAILURE);
			}
		}, imageExecutor);
	}

	public CompletableFuture<ResponseEntity<byte[]>> downloadImageAsync(String objectName) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.downloadImageAsync(objectName).get();
			} catch (Exception e) {
				log.error("이미지 다운로드 실패", e);
				throw new BusinessException(ImageErrorCode.DOWNLOAD_FAILURE);
			}
		}, imageExecutor);
	}
}