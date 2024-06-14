package com.nailcase.model.enums;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = Category.Deserializer.class)
public enum Category {
	NEWS, NOTICE;

	public static class Deserializer extends JsonDeserializer<Category> {
		@Override
		public Category deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
			String value = jsonParser.getText().toUpperCase();
			return Category.valueOf(value);
		}
	}
}
