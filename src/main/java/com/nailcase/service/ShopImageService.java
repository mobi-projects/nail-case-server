package com.nailcase.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.repository.ShopImageRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class ShopImageService extends ImageService<ShopImage> {
	private final ShopImageRepository shopImageRepository;

	public ShopImageService(AmazonS3 amazonS3, ShopImageRepository shopImageRepository) {
		super(amazonS3);
		this.shopImageRepository = shopImageRepository;
	}

	@Async("imageExecutor")
	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, ShopImage image) {
		return super.saveImageAsync(file, image, shopImageRepository);
	}

	@Async("imageExecutor")
	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<ShopImage> images) {
		return super.saveImagesAsync(files, images, shopImageRepository);
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