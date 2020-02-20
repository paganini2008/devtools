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
		public Short getValue(Boolean source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Short> characterConverter = new Converter<Character, Short>() {
		public Short getValue(Character source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Short> numberConverter = new Converter<Number, Short>() {
		public Short getValue(Number source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Short> stringConverter = new Converter<String, Short>() {
		public Short getValue(String source, Short defaultValue) {
			return Shorts.valueOf(source, defaultValue);
		}
	};

	public ShortConverter() {
		put(Boolean.class, booleanConverter);
		put(Character.class, characterConverter);
		put(Number.class, numberConverter);
		put(String.class, stringConverter);
	}
}
