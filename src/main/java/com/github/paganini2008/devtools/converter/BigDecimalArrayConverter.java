package com.github.paganini2008.devtools.converter;

import java.math.BigDecimal;

import com.github.paganini2008.devtools.math.BigDecimalUtils;

/**
 * BigDecimalArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigDecimalArrayConverter extends BaseConverter<BigDecimal[]> {

	private final Converter<byte[], BigDecimal[]> nativeByteArrayConverter = new Converter<byte[], BigDecimal[]>() {
		public BigDecimal[] getValue(byte[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	private final Converter<short[], BigDecimal[]> nativeShortArrayConverter = new Converter<short[], BigDecimal[]>() {
		public BigDecimal[] getValue(short[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	private final Converter<int[], BigDecimal[]> nativeIntArrayConverter = new Converter<int[], BigDecimal[]>() {
		public BigDecimal[] getValue(int[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	private final Converter<long[], BigDecimal[]> nativeLongArrayConverter = new Converter<long[], BigDecimal[]>() {
		public BigDecimal[] getValue(long[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	private final Converter<float[], BigDecimal[]> nativeFloatArrayConverter = new Converter<float[], BigDecimal[]>() {
		public BigDecimal[] getValue(float[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	private final Converter<double[], BigDecimal[]> nativeDoubleArrayConverter = new Converter<double[], BigDecimal[]>() {
		public BigDecimal[] getValue(double[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	private final Converter<Number[], BigDecimal[]> numberConverter = new Converter<Number[], BigDecimal[]>() {
		public BigDecimal[] getValue(Number[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	private final Converter<String[], BigDecimal[]> stringConverter = new Converter<String[], BigDecimal[]>() {
		public BigDecimal[] getValue(String[] source, BigDecimal[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigDecimalUtils.valuesOf(source);
		}
	};

	public BigDecimalArrayConverter() {
		put(byte[].class, nativeByteArrayConverter);
		put(short[].class, nativeShortArrayConverter);
		put(int[].class, nativeIntArrayConverter);
		put(long[].class, nativeLongArrayConverter);
		put(float[].class, nativeFloatArrayConverter);
		put(double[].class, nativeDoubleArrayConverter);
		put(Number[].class, numberConverter);
		put(String[].class, stringConverter);
	}

}
