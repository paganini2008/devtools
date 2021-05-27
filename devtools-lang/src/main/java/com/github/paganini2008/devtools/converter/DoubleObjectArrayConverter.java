package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.primitives.Doubles;

/**
 * DoubleObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DoubleObjectArrayConverter extends BasicConverter<Double[]> {

	private final Converter<CharSequence, Double[]> charSequenceConverter = new Converter<CharSequence, Double[]>() {
		public Double[] convertValue(CharSequence source, Double[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, delimiter);
			return result != null ? Doubles.valueOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Double[]> numberArrayConverter = new Converter<Number[], Double[]>() {
		public Double[] convertValue(Number[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.valueOf(source);
		}
	};

	private final Converter<String[], Double[]> stringArrayConverter = new Converter<String[], Double[]>() {
		public Double[] convertValue(String[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.valueOf(source);
		}
	};

	private final Converter<byte[], Double[]> byteArrayConverter = new Converter<byte[], Double[]>() {
		public Double[] convertValue(byte[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<short[], Double[]> shortArrayConverter = new Converter<short[], Double[]>() {
		public Double[] convertValue(short[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<char[], Double[]> charArrayConverter = new Converter<char[], Double[]>() {
		public Double[] convertValue(char[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<int[], Double[]> intArrayConverter = new Converter<int[], Double[]>() {
		public Double[] convertValue(int[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<long[], Double[]> longArrayConverter = new Converter<long[], Double[]>() {
		public Double[] convertValue(long[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<float[], Double[]> floatArrayConverter = new Converter<float[], Double[]>() {
		public Double[] convertValue(float[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<double[], Double[]> doubleArrayConverter = new Converter<double[], Double[]>() {
		public Double[] convertValue(double[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	public DoubleObjectArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberArrayConverter);
		registerType(String[].class, stringArrayConverter);
		registerType(byte[].class, byteArrayConverter);
		registerType(char[].class, charArrayConverter);
		registerType(short[].class, shortArrayConverter);
		registerType(int[].class, intArrayConverter);
		registerType(long[].class, longArrayConverter);
		registerType(float[].class, floatArrayConverter);
		registerType(double[].class, doubleArrayConverter);
	}

	private String delimiter = ",";

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
