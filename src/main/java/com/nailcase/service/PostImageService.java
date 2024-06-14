package com.nailcase.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.PostImage;
import com.nailcase.repository.PostImageRepository;

@Transactional
@Service
public class PostImageService extends ImageService<PostImage> {

	private final PostImageRepository postImageRepository;

	public PostImageService(AmazonS3 amazonS3, PostImageRepository postImageRepository) {
		super(amazonS3);
		this.postImageRepository = postImageRepository;
	}

	public ImageDto saveImage(MultipartFile file, PostImage image) {
		return super.saveImage(file, image, postImageRepository);
	}
}
