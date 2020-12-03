package com.github.paganini2008.devtools.converter;

import java.util.UUID;

import com.github.paganini2008.devtools.StringUtils;

/**
 * UUIDConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class UUIDConverter extends BasicConverter<UUID> {

	private final Converter<String, UUID> stringConverter = new Converter<String, UUID>() {
		public UUID convertValue(String source, UUID defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			return UUID.fromString(source);
		}
	};

	public UUIDConverter() {
		registerType(String.class, stringConverter);
	}

}
