package com.nailcase.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;
import com.nailcase.model.entity.PostImage;
import com.nailcase.repository.PostImageRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PostImageService extends ImageService<PostImage> {

	private final PostImageRepository postImageRepository;

	@Autowired
	private AsyncImageService asyncImageService;

	public PostImageService(AmazonS3 amazonS3, PostImageRepository postImageRepository) {
		super(amazonS3);
		this.postImageRepository = postImageRepository;
	}

	public List<ImageDto> saveImages(List<MultipartFile> files, List<PostImage> images) {
		if (files.size() != images.size()) {
			throw new BusinessException(ImageErrorCode.INVALID_FILE_TYPE, "파일 수와 이미지 엔티티 수가 일치하지 않습니다.");
		}

		List<CompletableFuture<ImageDto>> futures = new ArrayList<>();
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			PostImage image = images.get(i);
			CompletableFuture<ImageDto> future = asyncImageService.saveImageAsync(file, image, postImageRepository);
			futures.add(future);
		}

		// 모든 비동기 작업이 완료될 때까지 기다리고 결과를 수집

		return futures.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());
	}

	@Override
	public void deleteImage(String objectName) {
		asyncImageService.deleteImageAsync(objectName).join();
	}
}
