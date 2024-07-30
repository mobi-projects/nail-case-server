package com.nailcase.testUtils.fixture;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.mock.web.MockMultipartFile;

import com.nailcase.testUtils.StringGenerateFixture;

public class FileFixture {

	public MockMultipartFile getImageFile() {
		return new MockMultipartFile(
			StringGenerateFixture.makeByNumbersAndAlphabets(5),
			String.format("%s.jpg", StringGenerateFixture.makeByNumbersAndAlphabets(5)),
			"image/jpeg",
			StringGenerateFixture.makeByNumbersAndAlphabets().getBytes());
	}

	public List<MockMultipartFile> getImageFiles(int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> getImageFile())
			.collect(Collectors.toList());
	}

	public List<String> createBase64ImageFiles(int count) throws Exception {
		return IntStream.range(0, count)
			.mapToObj(i -> {
				try {
					byte[] content = Files.readAllBytes(Paths.get("path/to/image" + i + ".jpg"));
					return Base64.getEncoder().encodeToString(content);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toList());
	}

}
