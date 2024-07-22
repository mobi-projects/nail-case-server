package com.nailcase.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.PostImage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncImageService {

	private final ImageService imageService;

	@Async
	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, PostImage image,
		JpaRepository<PostImage, Long> imageRepository) {
		ImageDto savedImageDto = imageService.saveImage(file, image, imageRepository);
		return CompletableFuture.completedFuture(savedImageDto);
	}

	@Async
	public CompletableFuture<Void> uploadImageAsync(MultipartFile file, String objectName) {
		imageService.uploadImage(file, objectName);
		return CompletableFuture.completedFuture(null);
	}

	@Async
	public CompletableFuture<Void> deleteImageAsync(String objectName) {
		imageService.deleteImage(objectName);
		return CompletableFuture.completedFuture(null);
	}
}
