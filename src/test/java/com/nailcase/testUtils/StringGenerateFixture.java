package com.nailcase.testUtils;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * 테스트를 위해 다양한 형식의 문자열을 생성하는 유틸리티 클래스입니다.
 */
public final class StringGenerateFixture {
	private static final Random random = new Random();
	private static final int LEFT_LIMIT = '0';
	private static final int RIGHT_LIMIT = 'z';

	private static final int MIN = 1;
	private static final int MAX = 50;

	private static final String EMAIL_DOMAIN = "@test.com";

	private static final double MIN_LATITUDE = 33.0;
	private static final double MAX_LATITUDE = 39.0;
	private static final double MIN_LONGITUDE = 123;
	private static final double MAX_LONGITUDE = 132;

	/**
	 * 주어진 길이의 숫자와 알파벳으로 이루어진 문자열을 생성합니다.
	 *
	 * @param length 생성할 문자열의 길이
	 * @return 숫자와 알파벳으로 이루어진 문자열
	 */
	public static String makeByNumbersAndAlphabets(int length) {
		return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
			.filter(i -> ('0' <= i && i <= '9') || ('a' <= i && i <= 'z') || ('A' <= i && i <= 'Z'))
			.limit(length)
			.mapToObj(i -> String.valueOf((char)i))
			.collect(Collectors.joining());
	}

	/**
	 * 주어진 길이의 이메일 주소를 생성합니다.
	 *
	 * @param length 생성할 이메일 주소의 길이
	 * @return 이메일 주소
	 */
	public static String makeEmail(int length) {
		return makeByNumbersAndLowerLetters(length - EMAIL_DOMAIN.length()) + EMAIL_DOMAIN;
	}

	/**
	 * 주어진 길이의 숫자와 소문자로 이루어진 문자열을 생성합니다.
	 *
	 * @param length 생성할 문자열의 길이
	 * @return 숫자와 소문자로 이루어진 문자열
	 */
	public static String makeByNumbersAndLowerLetters(int length) {
		return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
			.filter(i -> ('0' <= i && i <= '9') || ('a' <= i && i <= 'z'))
			.limit(length)
			.mapToObj(i -> String.valueOf((char)i))
			.collect(Collectors.joining());
	}

	/**
	 * 난수로 결정된 길이의 숫자와 알파벳으로 이루어진 문자열을 생성합니다.
	 *
	 * @return 숫자와 알파벳으로 이루어진 문자열
	 */
	public static String makeByNumbersAndAlphabets() {
		int length = random.nextInt(MAX - MIN + 1) + MIN;
		return makeByNumbersAndAlphabets(length);
	}

	public static String makePoint() {
		return String.format(
			"%f,%f",
			MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * random.nextDouble(),
			MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * random.nextDouble());
	}
}
