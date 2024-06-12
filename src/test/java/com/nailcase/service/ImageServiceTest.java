package com.nailcase.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.ImageErrorCode;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;

public class ImageServiceTest {

	private ImageService imageService;
	private MinioClient minioClientMock;

	@BeforeEach
	void setUp() {
		minioClientMock = mock(MinioClient.class);
		imageService = new ImageService(minioClientMock);
	}

	@Test
	void uploadImage_Success() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());
		String bucketName = "test-bucket";
		String objectName = "test.jpg";

		doNothing().when(minioClientMock).makeBucket(any(MakeBucketArgs.class));
		when(minioClientMock.putObject(any(PutObjectArgs.class))).thenReturn(null);

		assertDoesNotThrow(() -> imageService.uploadImage(file, bucketName, objectName));
	}

	@Test
	void uploadImage_Failure() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
			"test image content".getBytes());
		String bucketName = "test-bucket";
		String objectName = "test.jpg";

		doThrow(new RuntimeException()).when(minioClientMock).putObject(any());

		BusinessException exception = assertThrows(BusinessException.class,
			() -> imageService.uploadImage(file, bucketName, objectName));
		assertEquals(ImageErrorCode.UPLOAD_FAILURE, exception.getErrorCode());
	}

	@Test
	void downloadImage_Success() throws Exception {
		String bucketName = "test-bucket";
		String objectName = "test.jpg";
		InputStream inputStream = new ByteArrayInputStream("test image content".getBytes());

		GetObjectResponse response = mock(GetObjectResponse.class);
		when(minioClientMock.getObject(any(GetObjectArgs.class))).thenReturn(response);
		when(response.object()).thenReturn(inputStream.toString());

		InputStream result = imageService.downloadImage(bucketName, objectName);
		assertNotNull(result);
		assertTrue(true);
	}

	@Test
	void downloadImage_Failure() throws Exception {
		String bucketName = "test-bucket";
		String objectName = "test.jpg";

		when(minioClientMock.getObject(any(GetObjectArgs.class))).thenThrow(
			new RuntimeException(new MinioException("Failed to download")));

		BusinessException exception = assertThrows(BusinessException.class,
			() -> imageService.downloadImage(bucketName, objectName));
		assertEquals(ImageErrorCode.DOWNLOAD_FAILURE, exception.getErrorCode());
	}

	@Test
	void deleteImage_Success() throws Exception {
		String bucketName = "test-bucket";
		String objectName = "test.jpg";

		doNothing().when(minioClientMock).removeObject(any());

		assertDoesNotThrow(() -> imageService.deleteImage(bucketName, objectName));
	}

	@Test
	void deleteImage_Failure() throws Exception {
		String bucketName = "test-bucket";
		String objectName = "test.jpg";

		doThrow(new RuntimeException(new MinioException("Failed to delete"))).when(minioClientMock).removeObject(any(
			RemoveObjectArgs.class));

		BusinessException exception = assertThrows(BusinessException.class,
			() -> imageService.deleteImage(bucketName, objectName));
		assertEquals(ImageErrorCode.DELETE_FAILURE, exception.getErrorCode());
	}
}
