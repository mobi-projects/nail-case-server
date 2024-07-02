package com.nailcase.util;

import java.util.regex.Pattern;

import com.nailcase.config.ValidationConfig;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericValidator implements ConstraintValidator<Validation, String> {
	private final ValidationConfig validationConfig;
	private Pattern pattern;
	private String msg;
	private String ruleName;

	@Override
	public void initialize(Validation constraintAnnotation) {
		this.ruleName = constraintAnnotation.ruleName();
		ValidationConfig.ValidationRule rule = validationConfig.getRule(ruleName);
		if (rule != null) {
			this.pattern = Pattern.compile(rule.getRegexp());
			this.msg = rule.getMsg();
		}
	}

	@Override
	public boolean isValid(String val, ConstraintValidatorContext context) {
		if (val == null || val.isEmpty() || pattern == null || msg == null) {
			return false;
		}

		if (!pattern.matcher(val).matches()) {
			setConstraintViolation(context, msg);
			return false;
		}

		if (ruleName.equals("point")) {
			return isValidPoint(val, context);
		}

		return true;
	}

	private boolean isValidPoint(String val, ConstraintValidatorContext context) {
		String[] points = val.split(",");
		if (points.length != 2) {
			setConstraintViolation(context, validationConfig.getRule("point").getMsg());
			return false;
		}

		String lonStr = points[0].trim();
		String latStr = points[1].trim();

		return isValidCoordinate(latStr, "lat", context) && isValidCoordinate(lonStr, "lon", context);
	}

	private boolean isValidCoordinate(String value, String ruleName, ConstraintValidatorContext context) {
		ValidationConfig.ValidationRule rule = validationConfig.getRule(ruleName);
		if (!Pattern.compile(rule.getRegexp()).matcher(value).matches()) {
			setConstraintViolation(context, rule.getMsg());
			return false;
		}

		try {
			double coordinate = Double.parseDouble(value);
			if (ruleName.equals("lat") && (coordinate < 33 || coordinate > 38)) {
				setConstraintViolation(context, "한국을 벗어난 위도");
				return false;
			}
			if (ruleName.equals("lon") && (coordinate < 124 || coordinate > 132)) {
				setConstraintViolation(context, "한국을 벗어난 경도");
				return false;
			}
		} catch (NumberFormatException e) {
			setConstraintViolation(context, "Invalid number format for " + ruleName);
			return false;
		}

		return true;
	}

	private void setConstraintViolation(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
			.addConstraintViolation();
	}
}
