package com.nailcase.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "validation")
public class ValidationConfig {
	private Map<String, ValidationRule> rules;

	public ValidationRule getRule(String ruleName) {
		return rules.get(ruleName);
	}

	@Data
	public static class ValidationRule {
		private String regexp;
		private String msg;
	}
}
