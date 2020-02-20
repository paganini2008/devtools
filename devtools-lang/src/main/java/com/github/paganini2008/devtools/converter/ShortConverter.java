package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Shorts;

/**
 * ShortConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ShortConverter extends BasicConverter<Short> {

	private final Converter<Boolean, Short> booleanConverter = new Converter<Boolean, Short>() {
		public Short convertValue(Boolean source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Short> characterConverter = new Converter<Character, Short>() {
		public Short convertValue(Character source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Short> numberConverter = new Converter<Number, Short>() {
		public Short convertValue(Number source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Short> stringConverter = new Converter<String, Short>() {
		public Short convertValue(String source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	public ShortConverter() {
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
	}
}
