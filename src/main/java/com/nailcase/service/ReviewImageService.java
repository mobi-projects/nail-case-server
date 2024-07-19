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
import com.nailcase.model.entity.ReviewImage;
import com.nailcase.repository.ReviewImageRepository;

@Transactional
@Service
public class ReviewImageService extends ImageService<ReviewImage> {

	private final ReviewImageRepository reviewImageRepository;

	public ReviewImageService(AmazonS3 amazonS3, ReviewImageRepository reviewImageRepository) {
		super(amazonS3);
		this.reviewImageRepository = reviewImageRepository;
	}

	@Async("imageExecutor")
	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, ReviewImage image) {
		return super.saveImageAsync(file, image, reviewImageRepository);
	}

	@Async("imageExecutor")
	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<ReviewImage> images) {
		return super.saveImagesAsync(files, images, reviewImageRepository);
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
