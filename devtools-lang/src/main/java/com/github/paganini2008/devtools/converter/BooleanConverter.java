package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Booleans;

/**
 * BooleanConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BooleanConverter extends BasicConverter<Boolean> {

	private final Converter<String, Boolean> booleanConverter = new Converter<String, Boolean>() {
		public Boolean getValue(String source, Boolean defaultValue) {
			return Booleans.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Boolean> characterConverter = new Converter<Character, Boolean>() {
		public Boolean getValue(Character source, Boolean defaultValue) {
			return Booleans.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Boolean> numberConverter = new Converter<Number, Boolean>() {
		public Boolean getValue(Number source, Boolean defaultValue) {
			return Booleans.valueOf(source, defaultValue);
		}
	};

	public BooleanConverter() {
		put(String.class, booleanConverter);
		put(Character.class, characterConverter);
		put(Number.class, numberConverter);
	}

}
