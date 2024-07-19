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
import com.nailcase.model.entity.PostImage;
import com.nailcase.repository.PostImageRepository;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class PostImageService extends ImageService<PostImage> {

	private final PostImageRepository postImageRepository;

	public PostImageService(AmazonS3 amazonS3, PostImageRepository postImageRepository) {
		super(amazonS3);
		this.postImageRepository = postImageRepository;
	}

	@Async("imageExecutor")
	public CompletableFuture<ImageDto> saveImageAsync(MultipartFile file, PostImage image) {
		return super.saveImageAsync(file, image, postImageRepository);
	}

	@Async("imageExecutor")
	public CompletableFuture<List<ImageDto>> saveImagesAsync(List<MultipartFile> files, List<PostImage> images) {
		return super.saveImagesAsync(files, images, postImageRepository);
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