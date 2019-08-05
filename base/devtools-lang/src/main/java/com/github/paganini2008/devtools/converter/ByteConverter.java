package com.github.paganini2008.devtools.converter;

import com.github.paganini2008.devtools.primitives.Bytes;

/**
 * ByteConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ByteConverter extends BaseConverter<Byte> {

	private final Converter<Boolean, Byte> booleanConverter = new Converter<Boolean, Byte>() {
		public Byte getValue(Boolean source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<Character, Byte> characterConverter = new Converter<Character, Byte>() {
		public Byte getValue(Character source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<Number, Byte> numberConverter = new Converter<Number, Byte>() {
		public Byte getValue(Number source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Byte> stringConverter = new Converter<String, Byte>() {
		public Byte getValue(String source, Byte defaultValue) {
			return Bytes.valueOf(source, defaultValue);
		}
	};

	public ByteConverter() {
		put(Boolean.class, booleanConverter);
		put(Character.class, characterConverter);
		put(Number.class, numberConverter);
		put(String.class, stringConverter);
	}
}
