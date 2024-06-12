package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.nailcase.exception.BusinessException;

public class ImageServiceTest {

	private ImageService imageService;

	@Mock
	private AmazonS3 amazonS3Mock;

	private String bucket;

	@BeforeEach
	void setUp() throws NoSuchFieldException, IllegalAccessException {
		MockitoAnnotations.openMocks(this);
		imageService = new ImageService(amazonS3Mock, null); // ImageRepository를 null로 설정

		Field bucketField = ImageService.class.getDeclaredField("bucket");
		bucketField.setAccessible(true);
		bucket = "your-bucket-name";
		bucketField.set(imageService, bucket); // Reflection을 사용하여 ImageService의 private 필드에 값을 설정
	}

	@Test
	void uploadImage_Success() {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());
		String objectName = "test.jpg";

		assertDoesNotThrow(() -> imageService.uploadImage(file, objectName));
		verify(amazonS3Mock, times(1)).putObject(eq(bucket), eq(objectName), any(InputStream.class),
			any(ObjectMetadata.class));
	}

	@Test
	void uploadImage_Failure() {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());
		String objectName = "test.jpg";

		doThrow(new RuntimeException()).when(amazonS3Mock)
			.putObject(eq(bucket), eq(objectName), any(InputStream.class), any(ObjectMetadata.class));

		assertThrows(BusinessException.class, () -> imageService.uploadImage(file, objectName));
	}

	@Test
	void downloadImage_Success() throws IOException {
		String objectName = "test.jpg";
		ByteArrayInputStream inputStream = new ByteArrayInputStream("test image content".getBytes());
		S3Object s3Object = mock(S3Object.class);
		when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(inputStream, null));
		when(amazonS3Mock.getObject(eq(bucket), eq(objectName))).thenReturn(s3Object);

		byte[] result = imageService.downloadImage(objectName);

		assertNotNull(result);
		verify(amazonS3Mock, times(1)).getObject(eq(bucket), eq(objectName));
	}

	@Test
	void downloadImage_Failure() {
		String objectName = "test.jpg";

		when(amazonS3Mock.getObject(eq(bucket), eq(objectName))).thenThrow(new RuntimeException());

		assertThrows(BusinessException.class, () -> imageService.downloadImage(objectName));
	}

	@Test
	void deleteImage_Success() {
		String objectName = "test.jpg";

		assertDoesNotThrow(() -> imageService.deleteImage(objectName));
		verify(amazonS3Mock, times(1)).deleteObject(eq(bucket), eq(objectName));
	}

	@Test
	void deleteImage_Failure() {
		String objectName = "test.jpg";

		doThrow(new RuntimeException()).when(amazonS3Mock).deleteObject(eq(bucket), eq(objectName));

		assertThrows(BusinessException.class, () -> imageService.deleteImage(objectName));
	}
}