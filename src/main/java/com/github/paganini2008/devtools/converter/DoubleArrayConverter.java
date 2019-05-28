package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.Doubles;
import com.github.paganini2008.devtools.StringUtils;

/**
 * DoubleArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DoubleArrayConverter extends BaseConverter<double[]> {

	private final Converter<CharSequence, double[]> charSequenceConverter = new Converter<CharSequence, double[]>() {
		public double[] getValue(CharSequence source, double[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Doubles.parses(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], double[]> stringArrayConverter = new Converter<String[], double[]>() {
		public double[] getValue(String[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.parses(source);
		}
	};

	private final Converter<Number[], double[]> numberArrayConverter = new Converter<Number[], double[]>() {
		public double[] getValue(Number[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<char[], double[]> nativeCharArrayConverter = new Converter<char[], double[]>() {
		public double[] getValue(char[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<boolean[], double[]> nativeBooleanArrayConverter = new Converter<boolean[], double[]>() {
		public double[] getValue(boolean[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<byte[], double[]> nativeByteArrayConverter = new Converter<byte[], double[]>() {
		public double[] getValue(byte[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<int[], double[]> nativeIntArrayConverter = new Converter<int[], double[]>() {
		public double[] getValue(int[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<long[], double[]> nativeLongArrayConverter = new Converter<long[], double[]>() {
		public double[] getValue(long[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<float[], double[]> nativeFloatArrayConverter = new Converter<float[], double[]>() {
		public double[] getValue(float[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	public DoubleArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
		put(char[].class, nativeCharArrayConverter);
		put(boolean[].class, nativeBooleanArrayConverter);
		put(byte[].class, nativeByteArrayConverter);
		put(int[].class, nativeIntArrayConverter);
		put(long[].class, nativeLongArrayConverter);
		put(float[].class, nativeFloatArrayConverter);
	}

}
