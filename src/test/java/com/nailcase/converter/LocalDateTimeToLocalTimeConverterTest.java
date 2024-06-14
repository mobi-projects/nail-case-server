package com.nailcase.converter;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class LocalDateTimeToLocalTimeConverterTest {

	private final int HOUR = 1;
	private final int MINUTE = 0;
	private AutoCloseable closeable;

	@InjectMocks
	private LocalDateTimeToLocalTimeConverter converter;

	@BeforeEach
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	public void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	@DisplayName("LocalDateTime을 LocalTime으로 변환하는 테스트")
	public void testConvertToDatabaseColumnSucceed() {
		// Given
		LocalDateTime now = LocalDateTime.of(2024, 1, 1, HOUR, MINUTE, 0);

		// When
		LocalTime localTime = converter.convertToDatabaseColumn(now);

		// Then
		assertThat(localTime).isEqualTo(LocalTime.of(HOUR, MINUTE));
	}

	@Test
	@DisplayName("LocalTime을 LocalDateTime으로 변환하는 테스트")
	public void testConvertToEntityAttributeSucceed() {
		// Given
		LocalTime localTime = LocalTime.of(HOUR, MINUTE);

		// When
		LocalDateTime localDateTime = converter.convertToEntityAttribute(localTime);

		// Then
		assertThat(localDateTime.toLocalTime()).isEqualTo(localTime);
		assertThat(localDateTime.toLocalDate()).isEqualTo(LocalDate.now());
	}
}
