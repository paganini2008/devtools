package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Ints;

/**
 * IntegerConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class IntegerConverter extends BasicConverter<Integer> {

	private final Converter<Boolean, Integer> booleanConverter = new Converter<Boolean, Integer>() {
		public Integer getValue(Boolean source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Integer> characterConverter = new Converter<Character, Integer>() {
		public Integer getValue(Character source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Integer> numberConverter = new Converter<Number, Integer>() {
		public Integer getValue(Number source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Integer> stringConverter = new Converter<String, Integer>() {
		public Integer getValue(String source, Integer defaultValue) {
			return Ints.valueOf(source, defaultValue);
		}
	};

	public IntegerConverter() {
		put(Boolean.class, booleanConverter);
		put(Character.class, characterConverter);
		put(Number.class, numberConverter);
		put(String.class, stringConverter);
	}

}
