package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * LongArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class LongArrayConverter extends BaseConverter<long[]> {

	private final Converter<CharSequence, long[]> charSequenceConverter = new Converter<CharSequence, long[]>() {
		public long[] getValue(CharSequence source, long[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Longs.parses(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], long[]> stringArrayConverter = new Converter<String[], long[]>() {
		public long[] getValue(String[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.parses(source);
		}
	};

	private final Converter<Number[], long[]> numberArrayConverter = new Converter<Number[], long[]>() {
		public long[] getValue(Number[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<char[], long[]> nativeCharArrayConverter = new Converter<char[], long[]>() {
		public long[] getValue(char[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<boolean[], long[]> nativeBooleanArrayConverter = new Converter<boolean[], long[]>() {
		public long[] getValue(boolean[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<byte[], long[]> nativeByteArrayConverter = new Converter<byte[], long[]>() {
		public long[] getValue(byte[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<short[], long[]> nativeShortArrayConverter = new Converter<short[], long[]>() {
		public long[] getValue(short[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<float[], long[]> nativeFloatArrayConverter = new Converter<float[], long[]>() {
		public long[] getValue(float[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	private final Converter<double[], long[]> nativeDoubleArrayConverter = new Converter<double[], long[]>() {
		public long[] getValue(double[] source, long[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Longs.casts(source);
		}
	};

	public LongArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(char[].class, nativeCharArrayConverter);
		put(boolean[].class, nativeBooleanArrayConverter);
		put(byte[].class, nativeByteArrayConverter);
		put(int[].class, nativeShortArrayConverter);
		put(float[].class, nativeFloatArrayConverter);
		put(double[].class, nativeDoubleArrayConverter);
	}

}
