package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Doubles;

/**
 * DoubleConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DoubleConverter extends BasicConverter<Double> {

	private final Converter<Number, Double> numberConverter = new Converter<Number, Double>() {
		public Double convertValue(Number source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Double> stringConverter = new Converter<String, Double>() {
		public Double convertValue(String source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Double> characterConverter = new Converter<Character, Double>() {
		public Double convertValue(Character source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	public DoubleConverter() {
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Character.class, characterConverter);
	}

}
