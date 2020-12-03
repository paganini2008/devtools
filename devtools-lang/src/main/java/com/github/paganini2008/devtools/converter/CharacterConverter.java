package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Chars;

/**
 * CharacterConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CharacterConverter extends BasicConverter<Character> {

	private final Converter<String, Character> stringConverter = new Converter<String, Character>() {
		public Character convertValue(String source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	private final Converter<Integer, Character> integerConverter = new Converter<Integer, Character>() {
		public Character convertValue(Integer source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	private final Converter<Boolean, Character> booleanConverter = new Converter<Boolean, Character>() {
		public Character convertValue(Boolean source, Character defaultValue) {
			return Chars.valueOf(source, defaultValue);
		}
	};

	public CharacterConverter() {
		registerType(String.class, stringConverter);
		registerType(Integer.class, integerConverter);
		registerType(Boolean.class, booleanConverter);
	}

}
