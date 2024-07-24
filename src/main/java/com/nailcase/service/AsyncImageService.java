package com.nailcase.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.common.Image;
import com.nailcase.common.dto.ImageDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncImageService {

	private final ImageService imageService;

	@Async
	public <T extends Image> CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, T image,
		JpaRepository<T, Long> imageRepository) {
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
