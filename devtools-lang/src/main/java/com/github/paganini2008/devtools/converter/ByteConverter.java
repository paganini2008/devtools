package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Bytes;

/**
 * ByteConverter
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ByteConverter extends BasicConverter<Byte> {

	private final Converter<Boolean, Byte> booleanConverter = new Converter<Boolean, Byte>() {
		public Byte convertValue(Boolean source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Byte> characterConverter = new Converter<Character, Byte>() {
		public Byte convertValue(Character source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Byte> numberConverter = new Converter<Number, Byte>() {
		public Byte convertValue(Number source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Byte> stringConverter = new Converter<String, Byte>() {
		public Byte convertValue(String source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	public ByteConverter() {
		registerType(Boolean.class, booleanConverter);
		registerType(Character.class, characterConverter);
		registerType(Number.class, numberConverter);
		registerType(String.class, stringConverter);
	}
}
