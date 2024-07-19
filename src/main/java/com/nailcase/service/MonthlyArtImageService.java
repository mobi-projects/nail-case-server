package com.nailcase.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.repository.MonthlyArtImageRepository;

@Transactional
@Service
public class MonthlyArtImageService extends ImageService<MonthlyArtImage> {

	private final MonthlyArtImageRepository monthlyArtImageRepository;

	public MonthlyArtImageService(AmazonS3 amazonS3, MonthlyArtImageRepository monthlyArtImageRepository) {
		super(amazonS3);
		this.monthlyArtImageRepository = monthlyArtImageRepository;
	}

	@Async("imageExecutor")
	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, MonthlyArtImage image) {
		return super.saveImageAsync(file, image, monthlyArtImageRepository);
	}

	@Async("imageExecutor")
	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<MonthlyArtImage> images) {
		return super.saveImagesAsync(files, images, monthlyArtImageRepository);
	}

	@Async("imageExecutor")
	public CompletableFuture<Void> deleteImageAsync(String objectName) {
		return super.deleteImageAsync(objectName);
	}

	@Async("imageExecutor")
	public CompletableFuture<ResponseEntity<byte[]>> downloadImageAsync(String objectName) {
		return super.downloadImageAsync(objectName);
	}
}
