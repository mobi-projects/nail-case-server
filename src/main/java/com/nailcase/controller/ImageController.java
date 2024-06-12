package com.nailcase.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nailcase.service.ImageService;

@RestController
@RequestMapping("/api/images")
public class ImageController {

	@Autowired
	private ImageService imageService;

	@PostMapping
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
		@RequestParam("bucketName") String bucketName,
		@RequestParam("objectName") String objectName) {
		imageService.uploadImage(file, bucketName, objectName);
		return ResponseEntity.ok("Image uploaded successfully");
	}

	@GetMapping("/{bucketName}/{objectName}")
	public ResponseEntity<InputStreamResource> downloadImage(@PathVariable("bucketName") String bucketName,
		@PathVariable("objectName") String objectName) throws IOException {
		InputStream inputStream = imageService.downloadImage(bucketName, objectName);
		InputStreamResource resource = new InputStreamResource(inputStream);
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_OCTET_STREAM)
			.body(resource);
	}

	@DeleteMapping("/{bucketName}/{objectName}")
	public ResponseEntity<String> deleteImage(@PathVariable("bucketName") String bucketName,
		@PathVariable("objectName") String objectName) {
		imageService.deleteImage(bucketName, objectName);
		return ResponseEntity.ok("Image deleted successfully");
	}
}