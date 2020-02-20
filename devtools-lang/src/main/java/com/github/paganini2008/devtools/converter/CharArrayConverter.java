package com.github.paganini2008.devtools.converter;

/**
 * CharArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CharArrayConverter extends BasicConverter<char[]> {

	private final Converter<CharSequence, char[]> charSequenceConverter = new Converter<CharSequence, char[]>() {
		public char[] getValue(CharSequence source, char[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return source.toString().toCharArray();
		}
	};

	public CharArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
	}

}
