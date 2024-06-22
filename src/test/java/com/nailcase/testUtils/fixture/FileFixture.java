package com.nailcase.testUtils.fixture;

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
}
