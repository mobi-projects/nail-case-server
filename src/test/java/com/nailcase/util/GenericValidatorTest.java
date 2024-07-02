package com.nailcase.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nailcase.config.ValidationConfig;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.Payload;

public class GenericValidatorTest {

	@Mock
	private ValidationConfig validationConfig;

	@InjectMocks
	private GenericValidator genericValidator;

	@Mock
	private ConstraintValidatorContext context;

	@Mock
	private ConstraintViolationBuilder builder;

	private AutoCloseable closeable;

	@BeforeEach
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		ValidationConfig.ValidationRule latRule = new ValidationConfig.ValidationRule();
		latRule.setRegexp("^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$");
		latRule.setMsg("Latitude must be between -90 and 90 degrees inclusive.");

		ValidationConfig.ValidationRule lonRule = new ValidationConfig.ValidationRule();
		lonRule.setRegexp("^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-7][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$");
		lonRule.setMsg("Longitude must be between -180 and 180 degrees inclusive.");

		ValidationConfig.ValidationRule pointRule = new ValidationConfig.ValidationRule();
		pointRule.setRegexp(".*,.*");
		pointRule.setMsg("Point must be an array of two elements with valid latitude and longitude values.");

		when(validationConfig.getRule("lat")).thenReturn(latRule);
		when(validationConfig.getRule("lon")).thenReturn(lonRule);
		when(validationConfig.getRule("point")).thenReturn(pointRule);

		when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
		when(builder.addConstraintViolation()).thenReturn(context);

		genericValidator.initialize(new Validation() {
			@Override
			public String ruleName() {
				return "point";
			}

			@Override
			public String message() {
				return "Invalid value";
			}

			@Override
			public Class<?>[] groups() {
				return new Class<?>[0];
			}

			@Override
			@SuppressWarnings("unchecked")
			public Class<? extends Payload>[] payload() {
				return new Class[0];
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return Validation.class;
			}
		});
	}

	@AfterEach
	public void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	@DisplayName("서울 위경도 성공 테스트")
	public void validPoint() {
		assertTrue(genericValidator.isValid("37.5665,126.9780", context));
	}
}
