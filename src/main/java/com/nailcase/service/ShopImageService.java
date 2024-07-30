package com.nailcase.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.repository.ShopImageRepository;

@Service
public class ShopImageService extends ImageService<ShopImage> {
	private final ShopImageRepository shopImageRepository;
	private final AsyncImageService asyncImageService;

	public ShopImageService(AmazonS3 amazonS3,
		ShopImageRepository shopImageRepository, AsyncImageService asyncImageService) {
		super(amazonS3);
		this.shopImageRepository = shopImageRepository;
		this.asyncImageService = asyncImageService;
	}

	public void deleteImage(String objectName) {
		asyncImageService.deleteImageAsync(objectName).join();
	}

	public CompletableFuture<ImageDto> uploadImage(MultipartFile file, ShopImage shopImage) {
		return asyncImageService.saveImageAsync(file, shopImage, shopImageRepository);
	}

	public void saveImagesSync(List<MultipartFile> files, List<ShopImage> images) {
		super.saveImagesSync(files, images, shopImageRepository);
	}

	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<ShopImage> images) {
		return asyncImageService.saveImagesAsync(files, images, shopImageRepository);
	}

}