package com.github.paganini2008.devtools.converter;

import java.math.BigInteger;

import com.github.paganini2008.devtools.math.BigIntegerUtils;

/**
 * BigIntegerArrayConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BigIntegerArrayConverter extends BasicConverter<BigInteger[]> {

	private final Converter<byte[], BigInteger[]> nativeByteArrayConverter = new Converter<byte[], BigInteger[]>() {
		public BigInteger[] getValue(byte[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valuesOf(source);
		}
	};

	private final Converter<short[], BigInteger[]> nativeShortArrayConverter = new Converter<short[], BigInteger[]>() {
		public BigInteger[] getValue(short[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valuesOf(source);
		}
	};

	private final Converter<int[], BigInteger[]> nativeIntArrayConverter = new Converter<int[], BigInteger[]>() {
		public BigInteger[] getValue(int[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valuesOf(source);
		}
	};

	private final Converter<long[], BigInteger[]> nativeLongArrayConverter = new Converter<long[], BigInteger[]>() {
		public BigInteger[] getValue(long[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valuesOf(source);
		}
	};

	private final Converter<Number[], BigInteger[]> numberArrayConverter = new Converter<Number[], BigInteger[]>() {
		public BigInteger[] getValue(Number[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valuesOf(source);
		}
	};

	private final Converter<String[], BigInteger[]> stringArrayConverter = new Converter<String[], BigInteger[]>() {
		public BigInteger[] getValue(String[] source, BigInteger[] defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			return BigIntegerUtils.valuesOf(source);
		}
	};

	public BigIntegerArrayConverter() {
		put(byte[].class, nativeByteArrayConverter);
		put(short[].class, nativeShortArrayConverter);
		put(int[].class, nativeIntArrayConverter);
		put(long[].class, nativeLongArrayConverter);
		put(Number[].class, numberArrayConverter);
		put(String[].class, stringArrayConverter);
	}

}
