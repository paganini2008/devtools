package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.Floats;

/**
 * FloatConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FloatConverter extends BaseConverter<Float> {

	private final Converter<Number, Float> numberConverter = new Converter<Number, Float>() {
		public Float getValue(Number source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Float> stringConverter = new Converter<String, Float>() {
		public Float getValue(String source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Float> characterConverter = new Converter<Character, Float>() {
		public Float getValue(Character source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	public FloatConverter() {
		put(Number.class, numberConverter);
		put(String.class, stringConverter);
		put(Character.class, characterConverter);
	}

}
