package com.nailcase.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeToLocalTimeConverter implements AttributeConverter<LocalDateTime, LocalTime> {

	@Override
	public LocalTime convertToDatabaseColumn(LocalDateTime attribute) {
		return attribute != null ? attribute.toLocalTime() : null;
	}

	@Override
	public LocalDateTime convertToEntityAttribute(LocalTime dbData) {
		return dbData != null ? LocalDateTime.of(LocalDate.now(), dbData) : null;
	}
}
