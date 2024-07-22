package com.nailcase.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.repository.ShopImageRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ShopImageService extends ImageService<ShopImage> {
	private final ShopImageRepository shopImageRepository;

	public ShopImageService(AmazonS3 amazonS3, ShopImageRepository shopImageRepository,
		@Qualifier("imageExecutor") Executor imageExecutor) {
		super(imageExecutor, amazonS3);
		this.shopImageRepository = shopImageRepository;
	}

	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, ShopImage image, Authentication auth) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.saveImageAsync(file, image, shopImageRepository, auth).get();
			} catch (Exception e) {
				log.error("이미지 저장 실패", e);
				throw new BusinessException(ImageErrorCode.SAVE_FAILURE);
			}
		}, imageExecutor);
	}

	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<ShopImage> images,
		Authentication auth) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.saveImagesAsync(files, images, shopImageRepository, auth).get();
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