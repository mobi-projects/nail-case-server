package com.nailcase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.model.entity.PriceImage;
import com.nailcase.repository.PriceImageRepository;

import jakarta.transaction.Transactional;

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

	public List<ImageDto> saveImages(List<MultipartFile> files, List<PriceImage> images) {
		if (files.size() != images.size()) {
			throw new BusinessException(ImageErrorCode.INVALID_FILE_TYPE, "파일 수와 이미지 엔티티 수가 일치하지 않습니다.");
		}

		List<CompletableFuture<ImageDto>> futures = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			PriceImage image = images.get(i);
			CompletableFuture<ImageDto> future = asyncImageService.saveImageAsync(file, image, priceImageRepository);
			futures.add(future);
		}

		return futures.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());
	}

	public void deleteImage(String objectName) {
		asyncImageService.deleteImageAsync(objectName).join();
	}

	public CompletableFuture<ImageDto> uploadImage(MultipartFile file, PriceImage priceImage) {
		return asyncImageService.saveImageAsync(file, priceImage, priceImageRepository);
	}
}