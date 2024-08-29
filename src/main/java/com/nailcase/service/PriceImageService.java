package com.nailcase.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.PriceImage;
import com.nailcase.repository.PriceImageRepository;

@Transactional
@Service
public class PriceImageService extends ImageService<PriceImage> {
	private final PriceImageRepository priceImageRepository;
	private final AsyncImageService asyncImageService;

	public PriceImageService(AmazonS3 amazonS3,
		PriceImageRepository priceImageRepository, AsyncImageService asyncImageService) {
		super(amazonS3);
		this.priceImageRepository = priceImageRepository;
		this.asyncImageService = asyncImageService;
	}

	public void deleteImage(String objectName) {
		asyncImageService.deleteImageAsync(objectName).join();
	}

	public CompletableFuture<ImageDto> uploadImage(MultipartFile file, PriceImage priceImage) {
		return asyncImageService.saveImageAsync(file, priceImage, priceImageRepository);
	}

	public void saveImagesSync(List<MultipartFile> files, List<PriceImage> images) {
		super.saveImagesSync(files, images, priceImageRepository);
	}

	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<PriceImage> images) {
		return asyncImageService.saveImagesAsync(files, images, priceImageRepository);
	}

}