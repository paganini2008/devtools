package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Floats;

/**
 * 
 * FloatObjectArrayConverter
 *
 * @author Fred Feng
 * @version 1.0
 */
public class FloatObjectArrayConverter extends BasicConverter<Float[]> {

	private final Converter<CharSequence, Float[]> charSequenceConverter = new Converter<CharSequence, Float[]>() {
		public Float[] convertValue(CharSequence source, Float[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, ",");
			return result != null ? Floats.valueOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Float[]> numberConverter = new Converter<Number[], Float[]>() {
		public Float[] convertValue(Number[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.valueOf(source);
		}
	};

	private final Converter<String[], Float[]> stringConverter = new Converter<String[], Float[]>() {
		public Float[] convertValue(String[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.valueOf(source);
		}
	};

	private final Converter<byte[], Float[]> byteArrayConverter = new Converter<byte[], Float[]>() {
		public Float[] convertValue(byte[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<short[], Float[]> shortArrayConverter = new Converter<short[], Float[]>() {
		public Float[] convertValue(short[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<char[], Float[]> charArrayConverter = new Converter<char[], Float[]>() {
		public Float[] convertValue(char[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<int[], Float[]> intArrayConverter = new Converter<int[], Float[]>() {
		public Float[] convertValue(int[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<float[], Float[]> floatArrayConverter = new Converter<float[], Float[]>() {
		public Float[] convertValue(float[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<long[], Float[]> longArrayConverter = new Converter<long[], Float[]>() {
		public Float[] convertValue(long[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	public FloatObjectArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberConverter);
		registerType(String[].class, stringConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(float[].class, floatArrayConverter);
	}

}
