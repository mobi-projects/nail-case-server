package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.nailcase.common.dto.ImageDto;
import com.nailcase.model.entity.Shop;
import com.nailcase.model.entity.ShopImage;
import com.nailcase.repository.ShopImageRepository;
import com.nailcase.testUtils.FixtureFactory;
import com.nailcase.testUtils.Reflection;
import com.nailcase.testUtils.StringGenerateFixture;
import com.nailcase.testUtils.fixture.FileFixture;
import com.nailcase.testUtils.fixture.ShopFixture;

@ExtendWith(MockitoExtension.class)
public class ShopImageServiceTest {
	private static final FileFixture fileFixture = FixtureFactory.fileFixture;
	private static final ShopFixture shopFixture = FixtureFactory.shopFixture;

	@Mock
	private AmazonS3 amazonS3;

	@Mock
	private ShopImageRepository shopImageRepository;

	@Mock
	private AsyncImageService asyncImageService;

	@InjectMocks
	private ShopImageService shopImageService;

	private String bucket;

	@BeforeEach
	void setUp() throws Exception {
		String bucket = StringGenerateFixture.makeByNumbersAndAlphabets(10);
		Reflection.setField(shopImageService, "bucket", bucket);
	}

	@Test
	@DisplayName("uploadImage 성공 테스트")
	void uploadImageSuccess() throws Exception {
		// Given
		MockMultipartFile file = fileFixture.getImageFile();
		Shop shop = shopFixture.getShop();

		ShopImage shopImage = ShopImage.builder().shop(shop).build();

		ImageDto expectedImageDto = ImageDto.builder()
			.bucketName(bucket)
			.objectName("test-object-name")
			.url("http://example.com/image.jpg")
			.build();

		when(asyncImageService.saveImageAsync(any(MultipartFile.class), any(ShopImage.class), eq(shopImageRepository)))
			.thenReturn(CompletableFuture.completedFuture(expectedImageDto));

		// When
		CompletableFuture<ImageDto> futureResult = shopImageService.uploadImage(file, shopImage);

		// Then
		ImageDto result = futureResult.get();
		assertNotNull(result);
		assertEquals(bucket, result.getBucketName());
		assertEquals("test-object-name", result.getObjectName());
		assertEquals("http://example.com/image.jpg", result.getUrl());

		verify(asyncImageService, times(1)).saveImageAsync(eq(file), eq(shopImage), eq(shopImageRepository));
	}
}
