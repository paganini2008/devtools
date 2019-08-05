package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Bytes;

/**
 * ByteObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ByteObjectArrayConverter extends BaseConverter<Byte[]> {

	private final Converter<CharSequence, Byte[]> charSequenceConverter = new Converter<CharSequence, Byte[]>() {
		public Byte[] getValue(CharSequence source, Byte[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Bytes.valuesOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<char[], Byte[]> nativeCharArrayConverter = new Converter<char[], Byte[]>() {
		public Byte[] getValue(char[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.toWrappers(source);
		}
	};

	private final Converter<boolean[], Byte[]> nativeBooleanArrayConverter = new Converter<boolean[], Byte[]>() {
		public Byte[] getValue(boolean[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.toWrappers(source);
		}
	};

	private final Converter<byte[], Byte[]> nativeByteArrayConverter = new Converter<byte[], Byte[]>() {
		public Byte[] getValue(byte[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.toWrappers(source);
		}
	};

	private final Converter<Number[], Byte[]> numberArrayConverter = new Converter<Number[], Byte[]>() {
		public Byte[] getValue(Number[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.valuesOf(source);
		}
	};

	private final Converter<String[], Byte[]> stringArrayConverter = new Converter<String[], Byte[]>() {
		public Byte[] getValue(String[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.valuesOf(source);
		}
	};

	public ByteObjectArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(boolean[].class, nativeBooleanArrayConverter);
		put(char[].class, nativeCharArrayConverter);
		put(byte[].class, nativeByteArrayConverter);
	}

}
