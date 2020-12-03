package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Chars;

/**
 * CharacterObjectArrayConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class CharacterObjectArrayConverter extends BasicConverter<Character[]> {

	private final Converter<char[], Character[]> charArrayConverter = new Converter<char[], Character[]>() {
		public Character[] convertValue(char[] source, Character[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Chars.toWrappers(source);
		}
	};

	private final Converter<CharSequence, Character[]> stringConverter = new Converter<CharSequence, Character[]>() {
		public Character[] convertValue(CharSequence source, Character[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Chars.toCharacterArray(source);
		}
	};

	public CharacterObjectArrayConverter() {
		registerType(char[].class, charArrayConverter);
		registerType(CharSequence.class, stringConverter);
	}

}
