package com.nailcase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.repository.MonthlyArtImageRepository;

@Transactional
@Service
public class MonthlyArtImageService extends ImageService<MonthlyArtImage> {

	private final MonthlyArtImageRepository monthlyArtImageRepository;
	private final AsyncImageService asyncImageService;

	public MonthlyArtImageService(AmazonS3 amazonS3,
		MonthlyArtImageRepository monthlyArtImageRepository,
		AsyncImageService asyncImageService) {
		super(amazonS3);
		this.monthlyArtImageRepository = monthlyArtImageRepository;
		this.asyncImageService = asyncImageService;
	}

	public List<ImageDto> saveImages(List<MultipartFile> files, List<MonthlyArtImage> images) {
		if (files.size() != images.size()) {
			throw new BusinessException(ImageErrorCode.INVALID_FILE_TYPE, "파일 수와 이미지 엔티티 수가 일치하지 않습니다.");
		}

		List<CompletableFuture<ImageDto>> futures = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			MonthlyArtImage image = images.get(i);
			CompletableFuture<ImageDto> future = asyncImageService.saveImageAsync(file, image,
				monthlyArtImageRepository);
			futures.add(future);
		}

		return futures.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());
	}

	public void deleteImage(String objectName) {
		asyncImageService.deleteImageAsync(objectName).join();
	}
}
