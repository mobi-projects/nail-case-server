package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

	@Test
	@DisplayName("uploadImageAsync 성공 테스트")
	void uploadImageAsyncSuccess() throws ExecutionException, InterruptedException {
		// Given
		MockMultipartFile file = fileFixture.getImageFile();
		Shop shop = shopFixture.getShop();
		ShopImage shopImage = ShopImage.builder().shop(shop).build();

		when(shopImageRepository.save(any(ShopImage.class))).thenReturn(shopImage);
		when(amazonS3.getUrl(anyString(), anyString())).thenReturn(mock(URL.class));

		// When
		CompletableFuture<ImageDto> futureResult = shopImageService.saveImageAsync(file, shopImage);

		// Then
		ImageDto result = futureResult.get(); // 비동기 작업이 완료될 때까지 대기

		assertNotNull(result);
		assertEquals(shopImage.getBucketName(), result.getBucketName());
		assertEquals(shopImage.getObjectName(), result.getObjectName());
		// 비동기 메서드 호출 확인
		verify(shopImageRepository, times(1)).save(any(ShopImage.class));

		ArgumentCaptor<String> bucketCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<InputStream> inputStreamCaptor = ArgumentCaptor.forClass(InputStream.class);
		ArgumentCaptor<ObjectMetadata> metadataCaptor = ArgumentCaptor.forClass(ObjectMetadata.class);

		verify(amazonS3, times(1)).putObject(
			bucketCaptor.capture(),
			keyCaptor.capture(),
			inputStreamCaptor.capture(),
			metadataCaptor.capture()
		);

		// S3 업로드 파라미터 검증
		assertEquals(shopImage.getBucketName(), bucketCaptor.getValue());
		assertTrue(keyCaptor.getValue().contains(file.getOriginalFilename()));
		assertNotNull(inputStreamCaptor.getValue());
		assertEquals(file.getSize(), metadataCaptor.getValue().getContentLength());
		assertEquals(file.getContentType(), metadataCaptor.getValue().getContentType());

		verify(amazonS3, times(1)).getUrl(anyString(), anyString());
	}

}
