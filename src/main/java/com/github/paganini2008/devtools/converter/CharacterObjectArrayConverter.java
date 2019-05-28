package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.Chars;

/**
 * CharacterObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharacterObjectArrayConverter extends BaseConverter<Character[]> {

	private final Converter<char[], Character[]> nativeCharArrayConverter = new Converter<char[], Character[]>() {
		public Character[] getValue(char[] source, Character[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Chars.toWrappers(source);
		}
	};

	private final Converter<CharSequence, Character[]> stringConverter = new Converter<CharSequence, Character[]>() {
		public Character[] getValue(CharSequence source, Character[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Chars.toCharacterArray(source);
		}
	};

	public CharacterObjectArrayConverter() {
		put(char[].class, nativeCharArrayConverter);
		put(CharSequence.class, stringConverter);
	}

}
