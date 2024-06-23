package com.nailcase.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.repository.ShopImageRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class ShopImageService extends ImageService<ShopImage> {
	private final ShopImageRepository shopImageRepository;

	public ShopImageService(AmazonS3 amazonS3, ShopImageRepository shopImageRepository) {
		super(amazonS3);
		this.shopImageRepository = shopImageRepository;
	}

	public ImageDto uploadImage(MultipartFile file, ShopImage shopImage) {
		return super.saveImage(file, shopImage, shopImageRepository);
	}
}