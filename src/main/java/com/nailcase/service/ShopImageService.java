package com.nailcase.service;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
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

}