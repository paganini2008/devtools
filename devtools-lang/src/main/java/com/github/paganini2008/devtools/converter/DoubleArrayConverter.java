package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Doubles;

/**
 * DoubleArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DoubleArrayConverter extends BasicConverter<double[]> {

	private final Converter<CharSequence, double[]> charSequenceConverter = new Converter<CharSequence, double[]>() {
		public double[] convertValue(CharSequence source, double[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Doubles.parseMany(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<String[], double[]> stringArrayConverter = new Converter<String[], double[]>() {
		public double[] convertValue(String[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.parseMany(source);
		}
	};

	private final Converter<Number[], double[]> numberArrayConverter = new Converter<Number[], double[]>() {
		public double[] convertValue(Number[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<char[], double[]> charArrayConverter = new Converter<char[], double[]>() {
		public double[] convertValue(char[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<boolean[], double[]> booleanArrayConverter = new Converter<boolean[], double[]>() {
		public double[] convertValue(boolean[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<byte[], double[]> byteArrayConverter = new Converter<byte[], double[]>() {
		public double[] convertValue(byte[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<int[], double[]> intArrayConverter = new Converter<int[], double[]>() {
		public double[] convertValue(int[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<long[], double[]> longArrayConverter = new Converter<long[], double[]>() {
		public double[] convertValue(long[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	private final Converter<float[], double[]> floatArrayConverter = new Converter<float[], double[]>() {
		public double[] convertValue(float[] source, double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.casts(source);
		}
	};

	public DoubleArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(boolean[].class, booleanArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(float[].class, floatArrayConverter);
	}

}
