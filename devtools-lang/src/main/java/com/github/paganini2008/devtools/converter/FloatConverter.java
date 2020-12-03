package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Floats;

/**
 * FloatConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FloatConverter extends BasicConverter<Float> {

	private final Converter<Number, Float> numberConverter = new Converter<Number, Float>() {
		public Float convertValue(Number source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Float> stringConverter = new Converter<String, Float>() {
		public Float convertValue(String source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Float> characterConverter = new Converter<Character, Float>() {
		public Float convertValue(Character source, Float defaultValue) {
			return Floats.valueOf(source, defaultValue);
		}
	};

	public FloatConverter() {
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
		registerType(Character.class, characterConverter);
	}

}
