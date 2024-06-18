package com.nailcase.service;

import java.util.List;

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

	public List<ImageDto> saveImages(List<MultipartFile> files, List<ReviewImage> images) {
		return super.saveImages(files, images, reviewImageRepository);
	}
}
