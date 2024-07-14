package com.nailcase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.getFactory().setCharacterEscapes(new CharacterEscapes() {
			private final int[] asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();

			@Override
			public int[] getEscapeCodesForAscii() {
				asciiEscapes['\n'] = CharacterEscapes.ESCAPE_NONE;
				asciiEscapes['\r'] = CharacterEscapes.ESCAPE_NONE;
				return asciiEscapes;
			}

			@Override
			public SerializableString getEscapeSequence(int ch) {
				if (ch > 0x7F) {
					return new SerializedString(String.format("\\u%04x", ch));
				} else {
					return null;
				}
			}
		});
		return objectMapper;
	}
}
