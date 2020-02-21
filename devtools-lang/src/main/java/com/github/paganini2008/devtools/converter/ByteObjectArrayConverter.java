package com.github.paganini2008.devtools.converter;

import java.nio.charset.Charset;

import com.github.paganini2008.devtools.CharsetUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Bytes;

/**
 * ByteObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ByteObjectArrayConverter extends BasicConverter<Byte[]> {

	private final Converter<CharSequence, Byte[]> charSequenceConverter = new Converter<CharSequence, Byte[]>() {
		public Byte[] convertValue(CharSequence source, Byte[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			byte[] bytes = source.toString().getBytes(charset);
			return Bytes.toWrappers(bytes);
		}
	};

	private final Converter<char[], Byte[]> charArrayConverter = new Converter<char[], Byte[]>() {
		public Byte[] convertValue(char[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.toWrappers(source);
		}
	};

	private final Converter<boolean[], Byte[]> booleanArrayConverter = new Converter<boolean[], Byte[]>() {
		public Byte[] convertValue(boolean[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.toWrappers(source);
		}
	};

	private final Converter<byte[], Byte[]> byteArrayConverter = new Converter<byte[], Byte[]>() {
		public Byte[] convertValue(byte[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.toWrappers(source);
		}
	};

	private final Converter<Number[], Byte[]> numberArrayConverter = new Converter<Number[], Byte[]>() {
		public Byte[] convertValue(Number[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.valueOf(source);
		}
	};

	private final Converter<String[], Byte[]> stringArrayConverter = new Converter<String[], Byte[]>() {
		public Byte[] convertValue(String[] source, Byte[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Bytes.valueOf(source);
		}
	};

	public ByteObjectArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(byte[].class, byteArrayConverter);
	}

	private Charset charset = CharsetUtils.DEFAULT;

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

}
