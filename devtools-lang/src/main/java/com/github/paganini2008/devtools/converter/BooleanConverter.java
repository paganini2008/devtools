package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Booleans;

/**
 * BooleanConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class BooleanConverter extends BasicConverter<Boolean> {

	private final Converter<String, Boolean> booleanConverter = new Converter<String, Boolean>() {
		public Boolean convertValue(String source, Boolean defaultValue) {
			return Booleans.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Boolean> characterConverter = new Converter<Character, Boolean>() {
		public Boolean convertValue(Character source, Boolean defaultValue) {
			return Booleans.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Boolean> numberConverter = new Converter<Number, Boolean>() {
		public Boolean convertValue(Number source, Boolean defaultValue) {
			return Booleans.valueOf(source, defaultValue);
		}
	};

	public BooleanConverter() {
		registerType(String.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
	}

}
