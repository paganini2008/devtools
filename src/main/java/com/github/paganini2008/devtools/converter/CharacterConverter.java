package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.Chars;

/**
 * CharacterConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharacterConverter extends BaseConverter<Character> {

	private final Converter<String, Character> stringConverter = new Converter<String, Character>() {
		public Character getValue(String source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	private final Converter<Integer, Character> integerConverter = new Converter<Integer, Character>() {
		public Character getValue(Integer source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, Character> booleanConverter = new Converter<Boolean, Character>() {
		public Character getValue(Boolean source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	public CharacterConverter() {
		put(String.class, stringConverter);
		put(Integer.class, integerConverter);
		put(Boolean.class, booleanConverter);
	}

}
