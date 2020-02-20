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
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Doubles.valueOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Double[]> numberConverter = new Converter<Number[], Double[]>() {
		public Double[] convertValue(Number[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.valueOf(source);
		}
	};

	private final Converter<String[], Double[]> stringConverter = new Converter<String[], Double[]>() {
		public Double[] convertValue(String[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.valueOf(source);
		}
	};

	private final Converter<byte[], Double[]> nativeByteArrayConverter = new Converter<byte[], Double[]>() {
		public Double[] convertValue(byte[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<short[], Double[]> nativeShortArrayConverter = new Converter<short[], Double[]>() {
		public Double[] convertValue(short[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<char[], Double[]> nativeCharArrayConverter = new Converter<char[], Double[]>() {
		public Double[] convertValue(char[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<int[], Double[]> nativeIntArrayConverter = new Converter<int[], Double[]>() {
		public Double[] convertValue(int[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<long[], Double[]> nativeLongArrayConverter = new Converter<long[], Double[]>() {
		public Double[] convertValue(long[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<float[], Double[]> nativeFloatArrayConverter = new Converter<float[], Double[]>() {
		public Double[] convertValue(float[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<double[], Double[]> nativeDoubleArrayConverter = new Converter<double[], Double[]>() {
		public Double[] convertValue(double[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	public DoubleObjectArrayConverter() {
		registerType(CharSequence.class, charSequenceConverter);
		registerType(Number[].class, numberConverter);
		registerType(String[].class, stringConverter);
		registerType(byte[].class, nativeByteArrayConverter);
		registerType(char[].class, nativeCharArrayConverter);
		registerType(short[].class, nativeShortArrayConverter);
		registerType(int[].class, nativeIntArrayConverter);
		registerType(long[].class, nativeLongArrayConverter);
		registerType(float[].class, nativeFloatArrayConverter);
		registerType(double[].class, nativeDoubleArrayConverter);
	}

}
