package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Floats;

public class FloatObjectArrayConverter extends BaseConverter<Float[]> {

	private final Converter<CharSequence, Float[]> charSequenceConverter = new Converter<CharSequence, Float[]>() {
		public Float[] getValue(CharSequence source, Float[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, ",");
			return result != null ? Floats.valuesOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Float[]> numberConverter = new Converter<Number[], Float[]>() {
		public Float[] getValue(Number[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.valuesOf(source);
		}
	};

	private final Converter<String[], Float[]> stringConverter = new Converter<String[], Float[]>() {
		public Float[] getValue(String[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.valuesOf(source);
		}
	};

	private final Converter<byte[], Float[]> nativeByteArrayConverter = new Converter<byte[], Float[]>() {
		public Float[] getValue(byte[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<short[], Float[]> nativeShortArrayConverter = new Converter<short[], Float[]>() {
		public Float[] getValue(short[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<char[], Float[]> nativeCharArrayConverter = new Converter<char[], Float[]>() {
		public Float[] getValue(char[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<int[], Float[]> nativeIntArrayConverter = new Converter<int[], Float[]>() {
		public Float[] getValue(int[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<float[], Float[]> nativeFloatArrayConverter = new Converter<float[], Float[]>() {
		public Float[] getValue(float[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	private final Converter<long[], Float[]> nativeLongArrayConverter = new Converter<long[], Float[]>() {
		public Float[] getValue(long[] source, Float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.toWrappers(source);
		}
	};

	public FloatObjectArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberConverter);
		put(String[].class, stringConverter);
		put(byte[].class, nativeByteArrayConverter);
		put(char[].class, nativeCharArrayConverter);
		put(short[].class, nativeShortArrayConverter);
		put(int[].class, nativeIntArrayConverter);
		put(long[].class, nativeLongArrayConverter);
		put(float[].class, nativeFloatArrayConverter);
	}

}
