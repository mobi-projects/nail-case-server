package com.nailcase.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

	// 현재 날짜를 문자열로 반환
	public static String getCurrentDate(String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDate.now().format(formatter);
	}

	// 현재 시간을 문자열로 반환
	public static String getCurrentDateTime(String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDateTime.now().format(formatter);
	}

	// 문자열을 LocalDate로 변환
	public static LocalDate parseDate(String dateStr, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDate.parse(dateStr, formatter);
	}

	// 문자열을 LocalDateTime으로 변환
	public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDateTime.parse(dateTimeStr, formatter);
	}

	// 두 날짜 사이의 일수 계산
	public static long daysBetween(LocalDate startDate, LocalDate endDate) {
		return ChronoUnit.DAYS.between(startDate, endDate);
	}

	// 날짜를 특정 패턴의 문자열로 변환
	public static String formatDate(LocalDate date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return date.format(formatter);
	}

	// 날짜와 시간을 특정 패턴의 문자열로 변환
	public static String formatDateTime(LocalDateTime dateTime, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return dateTime.format(formatter);
	}
}
