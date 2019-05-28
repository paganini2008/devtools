package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.Doubles;

/**
 * DoubleConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DoubleConverter extends BaseConverter<Double> {

	private final Converter<Number, Double> numberConverter = new Converter<Number, Double>() {
		public Double getValue(Number source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Double> stringConverter = new Converter<String, Double>() {
		public Double getValue(String source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Double> characterConverter = new Converter<Character, Double>() {
		public Double getValue(Character source, Double defaultValue) {
			return Doubles.valueOf(source, defaultValue);
		}
	};

	public DoubleConverter() {
		put(Number.class, numberConverter);
		put(String.class, stringConverter);
		put(Character.class, characterConverter);
	}

}
