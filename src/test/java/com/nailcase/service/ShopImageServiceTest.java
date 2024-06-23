package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
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

	@InjectMocks
	private ShopImageService shopImageService;

	@BeforeEach
	void setUp() throws Exception {
		String bucket = StringGenerateFixture.makeByNumbersAndAlphabets(10);
		Reflection.setField(shopImageService, "bucket", bucket);
	}

	@Test
	@DisplayName("uploadImage 성공 테스트")
	void uploadImageSuccess() {
		// Given
		MockMultipartFile file = fileFixture.getImageFile();
		Shop shop = shopFixture.getShop();

		ShopImage shopImage = ShopImage.builder().shop(shop).build();
		when(shopImageRepository.save(any(ShopImage.class))).thenReturn(shopImage);
		when(amazonS3.getUrl(anyString(), anyString())).thenReturn(mock(URL.class));

		// When
		ImageDto result = shopImageService.uploadImage(file, shopImage);

		// Then
		assertNotNull(result);
		assertEquals(shopImage.getBucketName(), result.getBucketName());
		assertEquals(shopImage.getObjectName(), result.getObjectName());

		verify(shopImageRepository, times(1)).save(any(ShopImage.class));
		verify(amazonS3, times(1))
			.putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class));
		verify(amazonS3, times(1)).getUrl(anyString(), anyString());
	}
}
