package com.github.paganini2008.devtools.converter;

import java.util.List;

import com.github.paganini2008.devtools.Doubles;
import com.github.paganini2008.devtools.StringUtils;

/**
 * DoubleObjectArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DoubleObjectArrayConverter extends BaseConverter<Double[]> {

	private final Converter<CharSequence, Double[]> charSequenceConverter = new Converter<CharSequence, Double[]>() {
		public Double[] getValue(CharSequence source, Double[] defaultValue) {
			if (StringUtils.isBlank(source)) {
				return defaultValue;
			}
			List<String> result = StringUtils.split(source, config.getDelimiter());
			return result != null ? Doubles.valuesOf(result.toArray(new String[result.size()])) : defaultValue;
		}
	};

	private final Converter<Number[], Double[]> numberConverter = new Converter<Number[], Double[]>() {
		public Double[] getValue(Number[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.valuesOf(source);
		}
	};

	private final Converter<String[], Double[]> stringConverter = new Converter<String[], Double[]>() {
		public Double[] getValue(String[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.valuesOf(source);
		}
	};

	private final Converter<byte[], Double[]> nativeByteArrayConverter = new Converter<byte[], Double[]>() {
		public Double[] getValue(byte[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<short[], Double[]> nativeShortArrayConverter = new Converter<short[], Double[]>() {
		public Double[] getValue(short[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<char[], Double[]> nativeCharArrayConverter = new Converter<char[], Double[]>() {
		public Double[] getValue(char[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<int[], Double[]> nativeIntArrayConverter = new Converter<int[], Double[]>() {
		public Double[] getValue(int[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<long[], Double[]> nativeLongArrayConverter = new Converter<long[], Double[]>() {
		public Double[] getValue(long[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<float[], Double[]> nativeFloatArrayConverter = new Converter<float[], Double[]>() {
		public Double[] getValue(float[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	private final Converter<double[], Double[]> nativeDoubleArrayConverter = new Converter<double[], Double[]>() {
		public Double[] getValue(double[] source, Double[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return Doubles.toWrappers(source);
		}
	};

	public DoubleObjectArrayConverter() {
		put(CharSequence.class, charSequenceConverter);
		put(Number[].class, numberConverter);
		put(String[].class, stringConverter);
		put(byte[].class, nativeByteArrayConverter);
		put(char[].class, nativeCharArrayConverter);
		put(short[].class, nativeShortArrayConverter);
		put(int[].class, nativeIntArrayConverter);
		put(long[].class, nativeLongArrayConverter);
		put(float[].class, nativeFloatArrayConverter);
		put(double[].class, nativeDoubleArrayConverter);
	}

}
