package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.Floats;
import com.github.paganini2008.devtools.StringUtils;

/**
 * FloatArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FloatArrayConverter extends BaseConverter<float[]> {

	private final Converter<CharSequence, float[]> charSequenceConverter = new Converter<CharSequence, float[]>() {
		public float[] getValue(CharSequence source, float[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Floats.parses(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], float[]> stringArrayConverter = new Converter<String[], float[]>() {
		public float[] getValue(String[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.parses(source);
		}
	};

	private final Converter<Number[], float[]> numberArrayConverter = new Converter<Number[], float[]>() {
		public float[] getValue(Number[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<char[], float[]> charArrayConverter = new Converter<char[], float[]>() {
		public float[] getValue(char[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<boolean[], float[]> booleanArrayConverter = new Converter<boolean[], float[]>() {
		public float[] getValue(boolean[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<byte[], float[]> byteArrayConverter = new Converter<byte[], float[]>() {
		public float[] getValue(byte[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<int[], float[]> intArrayConverter = new Converter<int[], float[]>() {
		public float[] getValue(int[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<long[], float[]> longArrayConverter = new Converter<long[], float[]>() {
		public float[] getValue(long[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	private final Converter<double[], float[]> doubleArrayConverter = new Converter<double[], float[]>() {
		public float[] getValue(double[] source, float[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Floats.casts(source);
		}
	};

	public FloatArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(char[].class, charArrayConverter);
		put(boolean[].class, booleanArrayConverter);
		put(byte[].class, byteArrayConverter);
		put(int[].class, intArrayConverter);
		put(long[].class, longArrayConverter);
		put(double[].class, doubleArrayConverter);
	}

}
