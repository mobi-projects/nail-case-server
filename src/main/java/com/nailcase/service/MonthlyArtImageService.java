package com.nailcase.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.MonthlyArtImage;
import com.nailcase.repository.MonthlyArtImageRepository;

@Transactional
@Service
public class MonthlyArtImageService extends ImageService<MonthlyArtImage> {

	private final MonthlyArtImageRepository monthlyArtImageRepository;

	public MonthlyArtImageService(AmazonS3 amazonS3, MonthlyArtImageRepository monthlyArtImageRepository) {
		super(amazonS3);
		this.monthlyArtImageRepository = monthlyArtImageRepository;
	}

	public List<ImageDto> saveImages(List<MultipartFile> files, List<MonthlyArtImage> images) {
		return super.saveImages(files, images, monthlyArtImageRepository);
	}
}
