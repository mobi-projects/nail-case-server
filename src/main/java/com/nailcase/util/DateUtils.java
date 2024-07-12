package com.nailcase.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRulesException;

import com.nailcase.exception.BusinessException;
import com.nailcase.exception.codes.UnixTimeErrorCode;

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

	// LocalDateTime을 Unix timestamp로 변환
	public static Long localDateTimeToUnixTimeStamp(LocalDateTime dateTime) {
		if (dateTime == null) {
			throw new BusinessException(UnixTimeErrorCode.NULL_DATETIME);
		}
		try {
			return dateTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
		} catch (ZoneRulesException e) {
			throw new BusinessException(UnixTimeErrorCode.CONVERSION_ERROR);
		}
	}

	/**
	 * Unix timestamp를 LocalDateTime로 변환합니다.
	 *
	 * @param timestamp Unix 타임스탬프 (초 단위)
	 * @return 변환된 LocalDateTime 객체
	 * @throws BusinessException 타임스탬프 값이 null이거나 음수, 혹은 설정된 최대 값보다 큰 경우
	 */
	public static LocalDateTime unixTimeStampToLocalDateTime(Long timestamp) {
		if (timestamp == null) {
			throw new BusinessException(UnixTimeErrorCode.NULL_TIMESTAMP);
		}
		if (timestamp < 0) {
			throw new BusinessException(UnixTimeErrorCode.NEGATIVE_TIMESTAMP);
		}
		// 최대 허용 가능 타임스탬프 설정 (9999년 12월 31일)
		long maxAllowedTimestamp = 253402300799L;
		if (timestamp > maxAllowedTimestamp) {
			throw new BusinessException(UnixTimeErrorCode.TIMESTAMP_TOO_LARGE);
		}
		try {
			return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
		} catch (Exception e) {
			throw new BusinessException(UnixTimeErrorCode.CONVERSION_ERROR);
		}
	}

	public static boolean isTimeBetween(LocalDateTime time, LocalDateTime start, LocalDateTime end) {
		return time.isAfter(start) && time.isBefore(end);
	}
}
