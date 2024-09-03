package com.nailcase.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {

	public List<Long> parseStringToLongList(String idsString) {
		if (idsString == null || idsString.isEmpty()) {
			return new ArrayList<>();
		}
		try {
			return Arrays.stream(idsString.replaceAll("\\[|\\]", "").split(","))
				.map(String::trim)
				.map(Long::parseLong)
				.collect(Collectors.toList());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid format for ids: " + idsString, e);
		}
	}

	public static String generateImageUrl(String bucket, String objectName) {
		// object 한글 인코딩
		return "https://" + bucket + ".s3.amazonaws.com/" + URLEncoder.encode(objectName, StandardCharsets.UTF_8);
	}
}
