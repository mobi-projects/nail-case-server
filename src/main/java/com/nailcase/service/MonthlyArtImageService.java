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
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.repository.MonthlyArtImageRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MonthlyArtImageService extends ImageService<MonthlyArtImage> {

	private final MonthlyArtImageRepository monthlyArtImageRepository;

	public MonthlyArtImageService(AmazonS3 amazonS3, MonthlyArtImageRepository monthlyArtImageRepository,
		@Qualifier("imageExecutor") Executor imageExecutor) {
		super(imageExecutor, amazonS3);
		this.monthlyArtImageRepository = monthlyArtImageRepository;
	}

	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, MonthlyArtImage image, Authentication auth) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.saveImageAsync(file, image, monthlyArtImageRepository, auth).get();
			} catch (Exception e) {
				log.error("이미지 저장 실패", e);
				throw new BusinessException(ImageErrorCode.SAVE_FAILURE);
			}
		}, imageExecutor);
	}

	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<MonthlyArtImage> images,
		Authentication auth) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.saveImagesAsync(files, images, monthlyArtImageRepository, auth).get();
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
